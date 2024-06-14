package servidor;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import ssoo.videos.PanelVisualizador;
import ssoo.videos.Transcodificador;
import ssoo.videos.Video;

public class HiloTranscodificador implements Runnable {

	private ColaDeTrabajos colaDeTrabajos;
	//private Video video;
	private Trabajo trabajo;
	private Transcodificador trascodificador;
	//private boolean okVideo = false; //false --> No aviso a hiloEncargo
	                            // true --> Aviso a HiloEncargo
	//private ConcurrentMap<Integer, Video>colaAux;
	//int aux;
	//private AtomicInteger aux;//Variable que gestiona las veces que 1 video se transcodifica
	
	
	public HiloTranscodificador(ColaDeTrabajos colaParametro) {
		this.colaDeTrabajos = colaParametro;
		trascodificador = new Transcodificador();
		//this.colaAux = colaAuxParam;
		//aux= 0;
		
		//aux = new AtomicInteger();//Vale 0 inicialmente
	}
	
	//************Anotaciones Fase 3*****************//
    //Lo primero que hace el hiloTranscodificador es extraer de la cola
	//Despues crea el video transcodificado, y el hilo de encargo a traves del 
	//objeto trabajo puede obtenerlo.
	
	//***********************************************//
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {//Bucle infinito que se ejecuta por siempre
			
			try {
				PanelVisualizador.getPanel().registrarColaTrabajos(colaDeTrabajos);
				trabajo = colaDeTrabajos.desencolarTrabajo();
				Video videoFinal = trascodificador.transcodificar(trabajo.getVideoInicial());
				trabajo.setVideoNuevo(videoFinal);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Codigo de Pruebas y cosas que no estaban del todo bien
			
			//colaAux.put(trabajo.getVideoInicial().hashCode(), trascodificador.transcodificar(trabajo.getVideoInicial()));
			// Esta linea ya no es necesaria al usar la clase Trabajo
			// Video videoTranscoficado = trascodificador.transcodificar(video);
            
		    //System.out.println("La clase HiloTranscodificador se ha ejecutado: "+aux.get());
			//aux.getAndIncrement();
			
			
			  System.out.println("Ha acabado de Transcodificar un video.");
			  
			
			
		}

	}
	


}
