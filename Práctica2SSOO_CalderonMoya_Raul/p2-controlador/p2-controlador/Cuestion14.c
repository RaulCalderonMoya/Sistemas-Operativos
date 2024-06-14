#include <sys/param.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sysexits.h>

#include <sys/wait.h>
#include <sys/types.h>
#include <errno.h>
#include <signal.h>

#define No_nacido 0
#define Vivo 1
#define Muerto 2
#define futura_muerte 3

typedef struct{
  pid_t pid_hijo;
  //0 -->NoNacido //1 --> Vivo //2 --> Muerto
  int estado;

} Hijo;

 static Hijo *TabladeHijos;
 static int terminar = 0;
 static int maximo_hijos;

//Cuestion 14 Raúl Calderón Moya  --> raul.calderon.moya@alumnos.upm.es
//y de Luis Mora Pincha-> luis.mora.pincha@alumnos.upm.es

static void matarProcesoHijo();
static void uso(void);
static void convertir(const char* fich_imagen, const char* dir_resultados);
static void manejador_sigterm(int signal){
   terminar = 1;
   printf("\n INiciando manejador sigterm:  %d \n", signal);
   matarProcesoHijo();
   printf("\n Se ha recibido la señal sigterm y estan todos los hijos muertos \n");
}


static void anotarMuerte(pid_t pid_hijo);
static int buscarPosicionVacia();


int main(int argc, char** argv)
{


	const char* dir_resultados;

    maximo_hijos=atoi(argv[1]);
	pid_t pid_hijo;
	int numero_hijos=0;
	int error;
	int estado = 0;
	int j = 0; //Variable que controla la posicion vacia
	int i = 3; //Se inicializa a 3 porque el numero de argumentos es a partir de 3 --> argv[3]
               //La variable i controla el numero de argumentos (i < argc)
	signal(SIGTERM, manejador_sigterm);
     //Le pasamos la señal sigterm, esto esta tal cual en el libro pagina 48
	if (argc < 4) {
		uso();
		exit(EX_USAGE);
	}

	printf("\n Iniciando aplicacion\n ");

	dir_resultados = argv[2];

		//Vamos a crear la tabla de hijos
	if((TabladeHijos = (Hijo*)calloc((size_t) maximo_hijos, sizeof(Hijo))) == NULL){
        perror("\n Error al crear la tabla de hijos\n ");
        exit (EX_OSERR);
	}


   //Inicializacion de la tabla
	for(j = 0; j < maximo_hijos ; j++){
        TabladeHijos[j].estado = No_nacido;
        printf("\n [%d]  Estado : %d  \n " , j, TabladeHijos[j].estado);
	}
      //i es el numero a partir del cual empieza a haber parametros de ahi
      //que su valor inicial sea 3, porque antes me pasaba que lo inicializaba a 0
      //y claro en lugar de coger la posicion inicial de los parametros cogia
      //la posicion argv[0] --> asignada al ejecutable en la linea de comandos


	while (i<argc && !terminar){
        switch(pid_hijo=fork()){
        case -1:
            perror ("\n Se ha producido un error al crear a los procesos hijos\n ");
            break;
        case 0:
            convertir(argv[i],dir_resultados);
            exit(0);
        default:
             //Fuerza la salida del while poniendo a la variable i el valor de los argumentos
             if(terminar == 1){
                i = argc;
             }

             //ES IMPORTANTE VER QUE POSICIONES ESTAN VACIAS, SE CONSIDERAN VACIAS AQUELLAS POSICIONES
            //Se considera vacia una posicion cuando Esta o Muerto o aun no ha vivido(NO_Nacido)
            j = buscarPosicionVacia();

            printf("\n Posicion Vacia = %d  \n ", j);
             //OJO esto lo pongo porque la funcion me exigia devolver si o si un entero
            //Con lo cual hay que poner -1 y si es distinto de -1 me devuelve la funcion
            // un valor adecuado de posicion vacia ya que si devuelve -1 entonces
            //es que ha entrado en los bucles de la funcion
            if (j != -1 ){
            TabladeHijos[j].estado = Vivo;
            TabladeHijos[j].pid_hijo = pid_hijo;


            numero_hijos++;
            i++;
            printf("\n Numero_hijos = %d \t  Numero de imagenes procesadas = %d \n ", numero_hijos , i-3);
            printf(" \n %d  Proceso hijo creado con PID = %d  pid padre = %d \n", numero_hijos,pid_hijo, getppid());
            while(numero_hijos==maximo_hijos && terminar == 0){
                do{
                    error=wait(&estado);
                }while(error==-1 && errno==EINTR);
                numero_hijos--;

                anotarMuerte(error);
                printf(" \n Proceso hijo muerto cuyo PID es %d\n",error);
            }

            }



        } //fin switch
	}//fin for de cuando el padre ha creado a todos los hijos
	//vamos a crear un bucle en el que el proceso padre espera a la muerte de los procesos hijos
	while (numero_hijos>0){
        do{
            error = wait(&estado);
        }while (error == -1 && errno == EINTR);
        numero_hijos--;
        printf("\n Proceso hijo muerto cuyo PID es %d\n",error);
	}
	exit(EX_OK);
}



static int buscarPosicionVacia(){
   for(int z = 0; z < maximo_hijos ; z ++){
      if(TabladeHijos[z].estado == Muerto || TabladeHijos[z].estado == No_nacido){
         return z;
      }

   }
  return -1; //Devuelvo -1 ya que si no entra en el if no devuelve un valor entero y luego vamos a evaluar si j == -1 en el main
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
	//Ponemos exec
	execlp("convert","convert", fich_imagen, "-blur", "0x8", nombre_destino, NULL);
}

//FUncion que sirve para anotar la defuncion
static void anotarMuerte(pid_t pid){
  for (int h = 0; h < maximo_hijos ; h++){
     if(TabladeHijos[h].pid_hijo == pid){
        TabladeHijos[h].estado = Muerto;
     }
  }

}

static void matarProcesoHijo(){
  for (int w = 0; w < maximo_hijos ; w++){
    if(TabladeHijos[w].estado == Vivo){
         TabladeHijos[w].estado = futura_muerte;
         kill(SIGTERM , TabladeHijos[w].pid_hijo);
    }

  }

}


