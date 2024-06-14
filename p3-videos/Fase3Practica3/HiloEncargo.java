package servidor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ssoo.videos.Dvd;
import ssoo.videos.MenuRaiz;
import ssoo.videos.Transcodificador;
//import ssoo.videos.Cola;
//import ssoo.videos.Encargo;
import ssoo.videos.Video;
import ssoo.videos.servidor.Cliente;
import ssoo.videos.servidor.Peticion;



public class HiloEncargo implements Runnable  {//Mejor poner que implementa Runnable aunque tambien vale poner extends Thread pero es mas facil con Runnable
    //Es mejor incluir directamente un Encargo en lugar de darlo por separado en numeroDeVideos y en nombre
    //Encargo encargo;
    //int numeroVideos;
    //String nombre;
    //String titulo;
	// private Encargo encargoRecibido;
	//private Cola colaDeTrabajos;
	private AtomicInteger numTrEncargo;//Numero de Trabajos del encargo
	//**Anotaciones Atomic Integer**************
    /*
     * La clase Atomic Integer nos sirve para que varios hilos puedan variar una variable del tipo enterp
     * ya sea un contador u otro tipo de entero, con lo cual varios hilos puedan acceder de manera
     * concurrente a dicha variable.
     * 
     * */	
	//****************************************//
	
	
	private Peticion peticion;
	private ColaDeTrabajos cola; 
	private Lock cerrojo;
	private Condition condicion;
	private ConcurrentMap <Integer, Video>colaAux; //De posible utilidad para la Fase 4
	private List<Trabajo> list;
	private AtomicInteger aux;//Variable que cuenta los videos que han entrado o los videos originales
	
	/*La variable AtomicInteger aux como tal no sirve para que la práctica funcione sino actua como sumador de videos originales 
	 * con esto podemos mirar cuantos videos originales tenemos y luego comparar con los videos transcodificados de los DVD que nos salen
	 * en la consola(cmd)
	 * Con ello he podido verificar que todos los videos que entran son los videos que salen transcodificados y no se pierden videos 
	 *  
	 * */
	
	/*AtomicIteger tiene dos metodos muy utiles:
	 * getAndIncrement() --> Incrementa el valor de manera concurrente de esa variable AtomicInteger
	 * 
	 * getAndDecrement() --> Decrementa el valor de manera concurrente.
	 * 
	 * get()--> Obtiene el valor entero que posee la variable aux
	 * */
	

	public HiloEncargo(Peticion peticionParametro,ColaDeTrabajos colaParametro){
		
		cerrojo = new ReentrantLock();
		condicion = cerrojo.newCondition();
		this.cola = colaParametro;
		this.peticion = peticionParametro;
		//this.colaAux = colaAuxParam;
		list = new ArrayList<Trabajo>();
		numTrEncargo= new AtomicInteger(); //Da al valor inicial el valor de 0 segun la API
		aux = new AtomicInteger();//Vale 0 inicialmente
	}

	
	public void run() {
		System.out.println("El número de vídeos del encargo son: "+peticion.getEncargo().getVideos().size());
        System.out.println("Nombre del usuario solicitante: "+peticion.getEncargo().getNombreUsuario());
		
        
        
        Trabajo primero ;
        
        
		try { //Con un solo try-catch puedo englobar todas las excepciones de estas lineas de codigo
		
	    //Trabajo trabajo = trabajo = new Trabajo(peticion.getEncargo().getVideos().get(pos), cerrojo, condicion); //Objeto trabajo
	     
	     
	    

		//Aqui recorremos los videos que tiene un encargo y se encarga de encolar todos los trabajos
		//del encargo de ahi que usemos peticion.getEncargo().getVideos().size()
		for (int j = 0; j < peticion.getEncargo().getVideos().size(); j++) {
			primero = new Trabajo(peticion.getEncargo().getVideos().get(j), cerrojo, condicion);
			
			aux.getAndIncrement();
		    cola.encolarTrabajo(primero);
		    //colaAux.put(trabajo.hashCode(), peticion.getEncargo().getVideos().get(pos));
		    list.add(primero);
			
		    //cola.encolarTrabajo(trabajo);
		    numTrEncargo.getAndIncrement(); //Equivale a incrementar valores del contador de los trabajos
		}

		List<Video> listaVideos = new ArrayList<Video>();
		cerrojo.lock();//INICIO DE LA SECCION CRITICA
		
		
		try {
			
			for (Trabajo trbjo : list)
			{
				if(trbjo.getVideoNuevo() != null) {
					//La clave está en coger posiciones no nulas para que no puedan
					//ocurrir excepciones de ningun tipo
					numTrEncargo.getAndDecrement();//Equivale a decrementar valores del contador de los trabajos
				}
			}
			
			
	        while(numTrEncargo.get()>0) {//Obtiene el valor de la variable contador
	        	condicion.await();
	        	//Nota: por cada señal recibida decrementa el valor de numero de trabajos
	        	//Cuando NumTrabajos es 0 significa que ya se han transcodificado todos
	        	numTrEncargo.getAndDecrement();//Equivale a decrementar valores del contador de los trabajos
	        }
			
			
		}finally {
			cerrojo.unlock(); //Fin seccion critica, es necesario recuadrarlo con try-catch
		}
		//Poner try-finally aqui puede evitar errores en el codigo.
		

        //Aqui está la línea que me permite resolver la duda de si salen tantos
		//videos como entran
		//Sabiendo el total de videos Iniciales se puede saber con los dvd que han
		//llegado cuántos y si coinciden o no.
        
		System.out.println("Han entrado : "+aux.get()+" videos Iniciales ");
		 
		//cerrojo.unlock(); //FIN SECCION CRITICA
		
		for(Trabajo t: list) {
			listaVideos.add(t.getVideoNuevo());
		}
		
		//Preparamos el DVD y le pasamos la lista de los Videos
		
		Dvd dvd = new Dvd("titulo", new MenuRaiz(listaVideos), listaVideos);
		Cliente user = peticion.getCliente();
		user.enviar(dvd);//Enviamos el Dvd

		
		System.out.println("Mensaje comprobador del fin"); //Mensaje de pruebas 
		
		}catch(Exception e) {
			e.printStackTrace();
		}
			
	}		
	

	//java -cp p3videos.jar ssoo.videos.clientes.ClienteAutomatizado
}	

