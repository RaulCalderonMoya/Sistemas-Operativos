package servidor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import ssoo.videos.Video;

public class Trabajo {
	private Video videoInicial;
	//Creamos una seccion critica con un cerrojo o mutex
	private Lock cerrojo;
	//Como va a ser una seccion critica condicional ,
	//creamos una condicion
	private Condition condicion;
	private Video videoNuevo;  //->Se corresponde con el video que ha sido Transcodificado
	                           //->a partir del uso de un video original
	
	public Trabajo(Video videoParametro, Lock cerrojoParametro, Condition condicionParametro) {
		this.videoInicial = videoParametro;
		this.cerrojo = cerrojoParametro;
		
		this.condicion = condicionParametro;
		videoNuevo = null;
	}
	
	//Para poder obtener estos valores y/o modificarlos 
    //vamos a crear los metodos getters and setters
   //En Source --> Generate Getters and Setters se puede hacer automaticamente
	public Video getVideoInicial() {
		return videoInicial;
	}

	public Lock getCerrojo() {
		return cerrojo;
	}

	public Condition getCondicion() {
		return condicion;
	}

	public void setCondicion(Condition condicion) {
		this.condicion = condicion;
	}

	public Video getVideoNuevo() {
		return videoNuevo;
	}

	public void setVideoNuevo(Video videoNuevo) {
		this.videoNuevo = videoNuevo;
	}
	
}
