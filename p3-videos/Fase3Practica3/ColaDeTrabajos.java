package servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ssoo.videos.Cola;
import ssoo.videos.Video;

public class ColaDeTrabajos implements Cola {
	
	//La cola de trabajos se caracteriza por permitir el encolado y desencolado
	//con lo cual puede meter y sacar de la cola
	private BlockingQueue<Trabajo> colaDeLosTrabajos;
	
	
	//1 Trabajo == 1 Video --> Esto Ya NO es asi ya lo explicó José Luis por qué(de ahí que haya que crearse la clase Trabajo)
	
	public ColaDeTrabajos(int maximoTrabajos) { //Le pasamos el maximo de trabajos que puede 
		                                         //almacenar la cola
		colaDeLosTrabajos = new ArrayBlockingQueue<Trabajo>(maximoTrabajos);
	}

	//Ahora debemos realizar las labores de encolado y desencolado
	public void encolarTrabajo(Trabajo v) throws Exception {
		colaDeLosTrabajos.put(v);
		
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
