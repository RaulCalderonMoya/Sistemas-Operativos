package servidor;

import java.util.concurrent.BlockingQueue;

import ssoo.videos.Cola;
import ssoo.videos.Encargo;
import ssoo.videos.servidor.Cliente;
import ssoo.videos.servidor.Peticion;

public class HiloEncargo implements Runnable {//Mejor poner que implementa Runnable aunque tambien vale poner extends Thread pero es mas facil con Runnable
     //Es mejor incluir directamente un Encargo en lugar de darlo por separado en numeroDeVideos y en nombre
     //Encargo encargo;
     //int numeroVideos;
     //String nombre;
     //String titulo;
	 private Encargo encargoRecibido;
	 //private Cola colaDeTrabajos;
     private ColaDeTrabajos cola;
	 
     //Se le pasa una peticion --> Numero de videos + nombre del usuario
	public HiloEncargo(Encargo encargo_parametro, ColaDeTrabajos colaParametro) {
        this.encargoRecibido = encargo_parametro; //encargo_parametro es el encargo que se recibe por parametro
        this.cola = colaParametro;
	}
	
	
	
	
	@Override
	public void run() {
		System.out.println("El número de vídeos del encargo son: "+encargoRecibido.getVideos().size());
        System.out.println("Nombre del usuario solicitante: "+encargoRecibido.getNombreUsuario());
		
        int aux = encargoRecibido.getVideos().size();
        
        for(int i = 0; i< aux ; i++) {
        	
        	try {
				cola.encolarTrabajo(encargoRecibido.getVideos().get(i));
				//Este paso lo mejor es que nos permite recorrer la lista de
				//videos con get y con la variable i podemos coger de posicion
				//en posicion.
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
        
        
        
        try {
			Thread.sleep(30000);//30*1000 ms = 30 s = 30000 ms
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}//Esta parte me exigía ponerla con un try-catch Eclipse
        
        System.out.println("Fin de la clase HiloEncargo.....");
        
        
	}
	//System.out.println("Hasta luego.........");
	 
	
}
