package labredes.servidor;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import labredes.worker.Worker;

public class Servidor {

  public final static String FILE_TO_SEND = "./serverData/";  // you may change this
  public final static int BUFFER_SIZE = 600000000;
  public final static int PACKET_SIZE = 1000;
  
  /**
	 * Constante que especifica el tiempo máximo en milisegundos que se esperara 
	 * por la respuesta de un cliente en cada una de las partes de la comunicación
	 */
	private static final int TIME_OUT = 100000;

	/**
	 * Constante que especifica el numero de threads que se usan en el pool de conexiones.
	 */
	public static final int N_THREADS = 1;

	/**
	 * Puerto en el cual escucha el servidor. 
	 |*/
	public static final int PUERTO = 4444;

	/**
	 * El socket que permite recibir requerimientos por parte de clientes.
	 */
	private static ServerSocket elSocket;
	private static Servidor elServidor;

	/**
	 * Metodo main del servidor con seguridad que inicializa un 
	 * pool de threads determinado por la constante nThreads.
	 * @param args Los argumentos del metodo main (vacios para este ejemplo).
	 * @throws IOException Si el socket no pudo ser creado.
	 */
	private ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
	public static void main(String[] args) throws IOException {
		elServidor = new Servidor();
		elServidor.runServidor();
	}
	
	private void runServidor() {

		int num = 0;
		try {
			// Crea el socket que escucha en el puerto seleccionado.
			elSocket = new ServerSocket();
			elSocket.setReceiveBufferSize(BUFFER_SIZE); 
			elSocket.setPerformancePreferences(0, 2, 1); //TODO Seleccionar los valores seg�n pruebas de carga 	 
			elSocket.bind(new InetSocketAddress(PUERTO)); 
			
			System.out.println("Servidor Coordinador escuchando en puerto: " + PUERTO);
			while (true) {
				Socket sThread = null;
				// ////////////////////////////////////////////////////////////////////////
				// Recibe conexiones de los clientes
				// ////////////////////////////////////////////////////////////////////////
				sThread = elSocket.accept();
				sThread.setSoTimeout(TIME_OUT); 
				System.out.println("Thread " + num + " recibe a un cliente.");
				executor.submit(new Worker(num,sThread));
				num++;
			}
		} catch (Exception e) {
			// No deberia alcanzarse en condiciones normales de ejecucion.
			e.printStackTrace();
		}
	}
 
  
}
