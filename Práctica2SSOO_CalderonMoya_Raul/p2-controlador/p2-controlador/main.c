#include <sys/param.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sysexits.h>

#include <sys/wait.h>
#include <sys/types.h>
#include <errno.h>

//Cuestión 13 de Raúl Calderón Moya -> raul.calderon.moya@alumnos.upm.es
// y de Luis Mora Pincha -> luis.mora.pincha@alumnos.upm.es

static void uso(void);
static void convertir(const char* fich_imagen, const char* dir_resultados);

int main(int argc, char** argv)
{
    //Anotacion nunca dar valor inicial a las variables
	const char* dir_resultados; //Creamos el directorio de resultados
	const int maximo_hijos=atoi(argv[1]); //Recuerda que el atoi transforma un char a un entero
	pid_t pid_hijo;
	int numero_hijos=0; //Variable que controla el numero de hijos
	int error;
	int estado;  //Variable que pasa la causa de la muerte del hijo, ojo mucho cuidado


	if (argc < 3) {
		uso();
		exit(EX_USAGE);
	}
	dir_resultados = argv[2];
	for (int i = 3; i < argc; i++){
        switch(pid_hijo=fork()){
        case -1:
            perror ("Se ha producido un error al crear a los procesos hijos");
            break;
        case 0:
            convertir(argv[i],dir_resultados);
            exit(0);
        default:
            numero_hijos++;
            printf("%d \n Proceso hijo creado con PID = %d", numero_hijos,pid_hijo);
            //Cuidado con el caso de ejecutarse solo 1 vez, modificar
            while(numero_hijos==maximo_hijos){
                do{
                    error=wait(&estado);
                }while(error==-1 && errno==EINTR);
                numero_hijos--;
                printf("Proceso hijo muerto cuyo PID es %d\n",error);
            }

        } //fin switch
	}//fin for de cuando el padre ha creado a todos los hijos
	//vamos a crear un bucle en el que el proceso padre espera a la muerte de los procesos hijos
	while (numero_hijos>0){
        do{
            error = wait(&estado);
        }while (error == -1 && errno == EINTR);
        numero_hijos--;
        printf("Proceso hijo muerto cuyo PID es %d\n",error);
	}
	exit(EX_OK);
}

static void uso(void)
{
	fprintf(stderr, "\nUso: paralelo dir_resultados fich_imagen...\n");
	fprintf(stderr, "\nEjemplo: paralelo difuminadas orig/*.jpg\n\n");
}

static void convertir(const char* fich_imagen, const char* dir_resultados)
{
	const char* nombre_base;
	char nombre_destino[MAXPATHLEN];
	char orden[MAXPATHLEN*3];

	nombre_base = strrchr(fich_imagen, '/');
	if (nombre_base == NULL)
		nombre_base = fich_imagen;
	else
		nombre_base++;
	snprintf(nombre_destino, sizeof(nombre_destino), "%s/%s", dir_resultados, nombre_base);
	snprintf(orden, sizeof(orden), "convert '%s' -blur 0x8 '%s'", fich_imagen, nombre_destino);
	//system(orden);
	//Cambiar esta ultima orden por exec
	execlp("convert", "convert", fich_imagen, "-blur", "0x8", nombre_destino, NULL);
}
