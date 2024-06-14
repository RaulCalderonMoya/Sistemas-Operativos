package servidor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Alertador {
	//Recordatorio: Para comunicar varios hilos utilizamos 
	//un cerrojo y una condicion
	private Lock mutex;
	//Nota: No se hace informador = new Condition(); ,sino que se hace
	//mutex.newCondition(); se utiliza el mutex para la instanciacion de la condicion
	
	private Condition informador;
	
	  public Alertador(Lock mutexParam, Condition parametroCondicion) {
			this.informador = parametroCondicion;
			this.mutex = mutexParam;
		}
	
	
	//public Alertador(Condition parametroCondicion, Lock mutexParam) {
		//this.informador = parametroCondicion;
		//this.mutex = mutexParam;
	//}
	
	public void alertarAlOtro() {
		mutex.lock();
		
		//Es necesario try-catch para hacer el mutex.unlock()
		//sino puede ser que falle el cerrojo
		//If any threads are waiting on this condition then they are all woken up. Each thread
		//must re-acquire the lock before it can return from await.
		//Esto quiere decir que cuando avisamos con signalAll()
		//despertamos a los que esperan
		try {
			//El objetivo de esta clase es informar al resto 
			//de ahi que usemos la sentencia signalAll
			informador.signalAll();
		}finally {
			mutex.unlock();
		}
		
		
	}
	

}
