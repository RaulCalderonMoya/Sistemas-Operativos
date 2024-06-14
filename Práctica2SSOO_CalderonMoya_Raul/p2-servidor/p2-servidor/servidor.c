#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>
#include <sysexits.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <errno.h>
#include <signal.h>

#define RES_FIN_CORRECTO 1
#define RES_ERROR 2
#define RES_DATO_INVALIDO 3

//Cuestion 28 de Raul Calderon Moya -> raul.calderon.moya@alumnos.upm.es
// y de Luis Mora Pincha -> luis.mora.pincha@alumnos.upm.es

//Ademas se han incluido las ultimas cuestiones resolviendo el problema de los procesos zombies(Cuestion29)

int activar_escucha(const int puerto_escucha);
int aceptar_cliente(const int s_escucha);
void atender_cliente(const int s_cliente);
int procesar_sesion(const int s_cliente);
long buscar_primo_superior(const long n);
int es_primo(const long n);

int numHijos = 0;

void manejador_SIGCHLD(int sig);

int main(int argc, char** argv)
{
	int puerto_escucha, s_escucha, s_cliente;

	if (argc == 1)
		puerto_escucha = 1234;
	else
		puerto_escucha = atoi(argv[1]);

	s_escucha = activar_escucha(puerto_escucha);
	if (s_escucha == -1) {
		fprintf(stderr, "ERROR: no se ha podido activar la escucha en el puerto %d.\n", puerto_escucha);
		fprintf(stderr, "Recomendaciones: mata todos los procesos de pruebas anteriores (servidor y nc). Como alternativa, prueba con un puerto diferente (> 1024).\n");
		exit(EX_UNAVAILABLE);
	}
	printf("INFO PID %d: escucho en el puerto %d.\n", getpid(), puerto_escucha);

	do {
		s_cliente = aceptar_cliente(s_escucha);
		if (s_cliente != -1)
			atender_cliente(s_cliente);
	} while (s_cliente != -1);

	close(s_escucha);
	exit(EX_OK);
}

void atender_cliente(const int s_cliente)
{
     //Recuerda no inicializar sin sentido las cosas
	//int res_proc = 0;
	  int res_proc;

	//Al igual que en controlador debemos hacer gestion de errores
	//a la hora de crear al hijo con lo cual

    switch (fork()){
        case (pid_t)-1:
		   //Caso de error al crear al proceso hijo

            perror ("Error no se ha podido crear el hijo");
            break;

        case (pid_t)0:
         // Caso del hijo
            res_proc = procesar_sesion(s_cliente);
            if (res_proc == RES_DATO_INVALIDO) {
                printf("Debe introducir informacion valida.\n");
            }

            exit(0);//RECUERDA QUE EN HIJO NUNCA SE PONE BREAK

        default:
    //La unica pega de este codigo es que los hijos al final se quedan en estado Z o zombies
   //Esta pega se solucionará después en la siguiente cuestion.
            fclose(fdopen(s_cliente, "r+"));
    }



	//CODIGO ANTIGUO QUE NO FUNCIONA TAL CUAL SE ESPECIFICA

	//if(!fork()){
      //  res_proc = procesar_sesion(s_cliente);
	//}else{
	  // perror("Error en el fork");
     //  exit(EX_UNAVAILABLE);
	///}



	//Codigo para codificar mas rapido
	//res_proc = procesar_sesion(s_cliente);
	//if (res_proc == RES_DATO_INVALIDO)
		//printf("AVISO: el cliente ha introducido un dato inválido.\n");
   //exit(EX_OK);
}


int procesar_sesion(const int s_cliente)
{
	FILE* f_cliente;
	char linea[100];
	long numero, primo_superior;
	int fin;

	printf("INFO PID %d: atiendo a cliente nuevo.\n", getpid());
	if ((f_cliente = fdopen(s_cliente, "r+")) == NULL) {
		perror("fdopen");
		return RES_ERROR;
	}
	setlinebuf(f_cliente);
	fprintf(f_cliente, "Bienvenido. Mi PID es %d.\n", getpid());
	do {
		fprintf(f_cliente, "Número: ");
		fin = (fgets(linea, sizeof linea, f_cliente) == NULL || strcmp(linea, "fin\n") == 0);
		if (! fin) {
			numero = atol(linea);
			if (numero > 0) {
				fprintf(f_cliente, "Calculando...\n");
				primo_superior = buscar_primo_superior(numero);
				fprintf(f_cliente, "%ld es el primer primo superior a %ld.\n", primo_superior, numero);
			}
		}
	} while (! fin && numero > 0);
	fprintf(f_cliente, "Adios\n");
	fclose(f_cliente);
	printf("INFO PID %d: fin de la sesión.\n", getpid());
	if (fin)
		return RES_FIN_CORRECTO;
	else
		return RES_DATO_INVALIDO;
}

int activar_escucha(const int puerto_escucha)
{
	int s_escucha;
	struct sockaddr_in sa_escucha;

	if ((s_escucha = socket(PF_INET, SOCK_STREAM, 0)) == -1) {
		perror("socket");
		return -1;
	}
	bzero(&sa_escucha, sizeof sa_escucha);
	sa_escucha.sin_family      = AF_INET;
	sa_escucha.sin_port        = htons(puerto_escucha);
	sa_escucha.sin_addr.s_addr = htonl(INADDR_ANY);
	if (bind(s_escucha, (struct sockaddr*) &sa_escucha, sizeof sa_escucha) == -1) {
		perror("bind");
		return -1;
	}
	if (listen(s_escucha, 10) == -1) {
		perror("listen");
		return -1;
	}
	return s_escucha;
}

int aceptar_cliente(const int s_escucha)
{
	int s_cliente;

	s_cliente = accept(s_escucha, NULL, NULL);
	if (s_cliente == -1)
		perror("accept");
	return s_cliente;
}

long buscar_primo_superior(const long n)
{
	long p;

	for (p = n+1; ! es_primo(p); p++);
	return p;
}

int es_primo(const long n)
{
	long i;

	for (i = 2; i < n; i++)
		if (n % i == 0)
			return 0;
	return 1;
}


//Codigo especifico para solucionar el problema de los hijos zombies(z)
//Este codigo pertenece a la Cuestion 29
//Se usa la funcion SIGCHILD de la pagina 49 del manual
//POSIX



void manejador_SIGCHLD(int sig){
    int estado;

    pid_t pid_hijo;


    //El proceso padre es el que captura SIGCHILD
    do{
        //SE USA EN ESTE CASO WAIT3 en el do-while
        pid_hijo = wait3(&estado, WNOHANG, NULL);
    } while (pid_hijo > 0);
}

