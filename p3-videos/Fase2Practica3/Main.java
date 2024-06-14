package servidor;

import java.io.IOException;

import ssoo.videos.Encargo;
import ssoo.videos.servidor.Peticion;
import ssoo.videos.servidor.ReceptorPeticiones;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("Bienvenido a mi sistema cliente-servidor");
		ReceptorPeticiones recepcion = new ReceptorPeticiones();
		//Ahora debemos ver la peticion que recibimos
		Peticion peticionRecibida;
		
		//Añadimos la cola de trabajos --> Se le pasa el valor
		//que nosotros hemos llamado maxTrabajos, no hay un valor fijo
		
		//Este valor que le introduzcamos indica el numero maximo
		//de elementos que puede haber en la cola de manera simultanea
		ColaDeTrabajos colaPrincipal = new ColaDeTrabajos(13);
		int numeroDeTranscoders = 7;
		
		
		//La clave aqui, es siempre tener en cuenta que el objeto compartido se pasa 
		//a la hora de instanciar clases con lo cual en esas respectivas clases habra que definirlo
		//tambien
		for(int i=0; i<numeroDeTranscoders; i++) {
			HiloTranscodificador TranscoderObject = new HiloTranscodificador(colaPrincipal);
			Thread hiloTr = new Thread(TranscoderObject);
			hiloTr.start();
		}
		
		
		
		//Aqui es donde creamos el nuevo hilo
		while(true) {//Bucle infinito
			peticionRecibida = recepcion.recibirPeticion();
			HiloEncargo encargo = new HiloEncargo(peticionRecibida.getEncargo(), colaPrincipal);
			
			Thread hilo = new Thread(encargo);
			hilo.start();
		}
		
		//A la hora de realizar la ejecucion debemos ejecutarlo en Practica3/lib
		//En esa ubicacion se debe ejecutar el comando del anexo II de la práctica

	}

}
