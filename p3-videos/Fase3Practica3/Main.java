package servidor;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ssoo.videos.Encargo;
import ssoo.videos.Video;
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
		ColaDeTrabajos colaPrincipal = new ColaDeTrabajos(10);
		int numeroDeTranscoders = 7;
		
		//De posible uso para la fase 4
		 //ConcurrentMap<Integer, Video>colaAuxPrincipal = new ConcurrentHashMap <Integer, Video>();
		
		//La clave aqui, es siempre tener en cuenta que el objeto compartido se pasa 
		//a la hora de instanciar clases con lo cual en esas respectivas clases habra que definirlo
		//tambien
		
		//Nota importante no se crean N transcodificadores  sino N-1 transcodificadores --> Importante
		for(int i=0; i<numeroDeTranscoders-1; i++) {
			HiloTranscodificador TranscoderObject = new HiloTranscodificador(colaPrincipal);
			Thread hiloTr = new Thread(TranscoderObject);
			hiloTr.start();
		}
		
		
		
		//Aqui es donde creamos el nuevo hilo
		while(true) {//Bucle infinito
			peticionRecibida = recepcion.recibirPeticion();
			
			System.out.println("Creacion de un  hilo de Encargo");//Linea de Pruebas
			HiloEncargo encargo = new HiloEncargo(peticionRecibida, colaPrincipal);
			
			Thread hilo = new Thread(encargo);
			hilo.start();
		}
		
		//A la hora de realizar la ejecucion debemos ejecutarlo en Practica3/lib
		//En esa ubicacion se debe ejecutar el comando del anexo II de la práctica

	}

}
