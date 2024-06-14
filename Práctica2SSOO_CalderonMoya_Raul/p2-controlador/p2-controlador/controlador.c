#include <sys/param.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sysexits.h>

static void uso(void);
static void convertir(const char* fich_imagen, const char* dir_resultados);

int main(int argc, char** argv)
{
	const char* dir_resultados;

	if (argc < 3) {
		uso();
		exit(EX_USAGE);
	}
	dir_resultados = argv[1];
	for (int i = 2; i < argc; i++)
		convertir(argv[i], dir_resultados);
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
	system(orden);
}
