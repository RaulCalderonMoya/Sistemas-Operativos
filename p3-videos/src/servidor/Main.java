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
		
		
		//Aqui es donde creamos el nuevo hilo
		while(true) {//Bucle infinito
			peticionRecibida = recepcion.recibirPeticion();
			HiloEncargo encargo = new HiloEncargo(peticionRecibida.getEncargo());
			
			Thread hilo = new Thread(encargo);
			hilo.start();
		}
		
		//A la hora de realizar la ejecucion debemos ejecutarlo en Practica3/lib
		//En esa ubicacion se debe ejecutar el comando del anexo II de la práctica

	}

}
