package servidor;

import ssoo.videos.Transcodificador;
import ssoo.videos.Video;

public class HiloTranscodificador implements Runnable {

	private ColaDeTrabajos colaDeTrabajos;
	private Video video;
	private Transcodificador trascodificador;
	
	
	public HiloTranscodificador(ColaDeTrabajos colaParametro) {
		this.colaDeTrabajos = colaParametro;
		trascodificador = new Transcodificador();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				video = colaDeTrabajos.desencolarTrabajo();
				
				Video videoTranscoficado = trascodificador.transcodificar(video);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Video videoTranscoficado = trascodificador.transcodificar(video);
			//Aqui nos dimos cuenta que lo mejor seria meterlo dentro del mismo try-catch
			
		}

	}

}
