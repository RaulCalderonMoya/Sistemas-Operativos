package servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import ssoo.videos.Cola;
import ssoo.videos.Video;

public class ColaDeTrabajos implements Cola {
	
	//La cola de trabajos se caracteriza por permitir el encolado y desencolado
	//con lo cual puede meter y sacar de la cola
	private BlockingQueue<Trabajo> colaDeLosTrabajos;
	
	
	//Creamos para la fase 4 un mapa que pueda devolver clave-valor
	//de lo que deseemos, en este caso vamos a usar un tipo ConcurrentHashMap
	//Que contiene como clave un Video(Video Original y de valor un Objeto Trabajo)
	//Con ello, podemos saber si hay o no trabajos encolados previamente.
	
	private ConcurrentHashMap<Video, Trabajo> colaMapaAuxiliar;//Nota: Importante ver que la clave no es un Integer sino un Video
	
	//1 Trabajo == 1 Video --> Esto Ya NO es asi ya lo explicó José Luis por qué(de ahí que haya que crearse la clase Trabajo)
	
	public ColaDeTrabajos(int maximoTrabajos) { //Le pasamos el maximo de trabajos que puede 
		                                         //almacenar la cola
		colaDeLosTrabajos = new ArrayBlockingQueue<Trabajo>(maximoTrabajos);
		colaMapaAuxiliar =  new ConcurrentHashMap<Video,Trabajo>();
	}

	//Ahora debemos realizar las labores de encolado y desencolado
	public Trabajo encolarTrabajo (Trabajo t) {
		Trabajo encolador = colaMapaAuxiliar.putIfAbsent(t.getVideoInicial(), t);
		if (encolador == null) {//Si el trabajo es null esto es lo que se realiza
			              //de lo contrario obtenemos el video que no ha sido
			              //transcodificado y lo devolvemos, esto se hace para 
			              //saber si ya ha sido o no encolado previamente
			try {
				colaDeLosTrabajos.put(t);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
				//Si llegamos a este punto significa que no se ha agregado el video
				//correctamente, con lo cual voy a añadir un mensaje para poder monitorear
				//cualquier tipo de fallo de agregacion
				System.out.println("Ha fallado la agregacion de video a la cola");
			}
		}
		encolador = colaMapaAuxiliar.get(t.getVideoInicial());
		return encolador;
	}
	
	public Trabajo desencolarTrabajo() throws Exception {
		Trabajo work;
		//Aqui se puede o incluir en un try catch aunque
		//permite la opcion de añadir throws en lugar de try-catch
		work = colaDeLosTrabajos.take();
		return work;
	}
	

	
	
	@Override
	public int numTrabajos() {
		// TODO Auto-generated method stub
		//Simplemente devuelve el numero de cosas que hay en la cola --> size()
		return colaDeLosTrabajos.size();
	}

}
