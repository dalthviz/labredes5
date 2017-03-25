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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import labredes.worker.Worker;

public class Servidor {

  public final static int SOCKET_PORT = 13267;  // you may change this
  public final static String FILE_TO_SEND = "C:/Users/Daniel/Desktop/Redes/Laboratorio 5/labredes5/README.md";  // you may change this

  
  /**
	 * Constante que especifica el tiempo máximo en milisegundos que se esperara 
	 * por la respuesta de un cliente en cada una de las partes de la comunicación
	 */
	private static final int TIME_OUT = 10000;

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
		int fallos = 0;
		try {
			// Crea el socket que escucha en el puerto seleccionado.
			elSocket = new ServerSocket(PUERTO);
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
			fallos++;
			// No deberia alcanzarse en condiciones normales de ejecucion.
			e.printStackTrace();
		}
	}
  
  
  
  public void sendFile() throws IOException {
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    ServerSocket servsock = null;
    Socket sock = null;
    PrintWriter escritor = null;
    BufferedReader lector = null;
    String fromServer;
    String fromClient;
    boolean conexion = true;
    boolean transferencia = true;
    try {
      servsock = new ServerSocket(SOCKET_PORT);
      
      while(conexion){

          System.out.println("Waiting...");
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);
          conexion = !sock.isConnected();
      }
      
      while (transferencia) {
        try {
          escritor = new PrintWriter(sock.getOutputStream(), true);
          lector = new BufferedReader(new InputStreamReader(
    				sock.getInputStream()));
        
          
          fromClient = lector.readLine();
          System.out.println(fromClient);
          // send file
          if(fromClient.equals("ENVIAR")){

        	  fromServer = "ENVIANDO";
              escritor.println(fromServer);
                  
              File myFile = new File (FILE_TO_SEND);
              byte [] mybytearray  = new byte [(int)myFile.length()];
              fis = new FileInputStream(myFile);
              bis = new BufferedInputStream(fis);
              bis.read(mybytearray,0,mybytearray.length);
              os = sock.getOutputStream();
              System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
              os.write(mybytearray,0,mybytearray.length);
              os.flush();
              System.out.println("Done.");             
        	  
          }
          
          if(fromClient.equals("OK"))
          {
        	  transferencia = false;
        	  fromServer = "OK";
        	  escritor.println(fromServer);
          }
          
          }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (sock!=null) sock.close();
        }
      }
    }
    finally {
      if (servsock != null) servsock.close();
    }
  }
}
