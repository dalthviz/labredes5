package labredes.worker;

import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import labredes.servidor.Servidor;


/**
 * Esta clase implementa el protocolo que se realiza al recibir una conexión de un cliente.
 * Infraestructura Computacional Universidad de los Andes. 
 * Las tildes han sido eliminadas por cuestiones de compatibilidad.
 * 
 * @author Cristian Fabián Brochero 		-  201620.
 */
public class Worker implements Runnable {

	// ----------------------------------------------------
	// CONSTANTES DE CONTROL DE IMPRESION EN CONSOLA
	// ----------------------------------------------------
	public static final boolean SHOW_ERROR = true;
	public static final boolean SHOW_S_TRACE = true;
	public static final boolean SHOW_IN = true;
	public static final boolean SHOW_OUT = true;
	// ----------------------------------------------------
	// CONSTANTES PARA LA DEFINICION DEL PROTOCOLO
	// ----------------------------------------------------
	public static final String OK = "OK";
	public static final String EMPEZANDO_TRANSFERENCIA = "Empezando transferencia de archivo";
	public static final String PARANDO_TRANSFERENCIA = "Parando transferencia de archivo";
	public static final String ARCHVOS = "Archivos";
	public static final String ARCHVO_SELECCIONADO = "Archivo seleccionado";
	public static final String TAMANO = "Tamaño archivo seleccionado";
	public static final String PARAR = "Parar transferencia";
	public static final String SEGUIR = "Seguir transferencia";
	public static final String EMPEZAR = "Emperzar transferencia";
	public static final String SEPARADOR = ":";
	public static final String HOLA = "HOLA";
	public static final String INIT = "INIT";
	public static final String RTA = "RTA";
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";
	public static final String ERROR_FORMATO = "Error en el formato. Cerrando conexion";
	
	private int id;
	private Socket ss;
	//private KeyPair keyPair;
	
	public Worker(int pId, Socket pSocket) {
		id = pId;
		ss = pSocket;
	}
	
	/**
	 * Metodo que se encarga de imprimir en consola todos los errores que se 
	 * producen durante la ejecuación del protocolo. 
	 * Ayuda a controlar de forma rapida el cambio entre imprimir y no imprimir este tipo de mensaje
	 */
	private static void printError(Exception e) {
		if(SHOW_ERROR)		System.out.println(e.getMessage());
		if(SHOW_S_TRACE) 	e.printStackTrace();	
	}

	/**
	 * Metodo que se encarga de leer los datos que envia el punto de atencion.
	 *  Ayuda a controlar de forma rapida el cambio entre imprimir y no imprimir este tipo de mensaje
	 */
	private String read(BufferedReader reader) throws IOException {
		String linea = reader.readLine();
		if(SHOW_IN)			System.out.println("Thread " + id + "<<CLNT: (recibe) " + linea);
		return linea;
		
	}

	/**
	 * Metodo que se encarga de escribir los datos que el servidor envia el punto de atencion.
	 *  Ayuda a controlar de forma rapida el cambio entre imprimir y no imprimir este tipo de mensaje
	 */
	private void write(PrintWriter writer, String msg) {
		writer.println(msg);
		if(SHOW_OUT)		System.out.println("Srv " + id + ">>SERV (envia): " + msg);
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
	      servsock = new ServerSocket(Servidor.PUERTO);
	      
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
	                  
	              File myFile = new File (Servidor.FILE_TO_SEND);
	              byte [] mybytearray  = new byte [(int)myFile.length()];
	              fis = new FileInputStream(myFile);
	              bis = new BufferedInputStream(fis);
	              bis.read(mybytearray,0,mybytearray.length);
	              os = sock.getOutputStream();
	              System.out.println("Sending " + Servidor.FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
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
	
	/**
	 * Metodo que establece el protocolo de comunicacion con el punto de atencion.
	  */

	public void run(){
		try{
			
			PrintWriter writer = new PrintWriter(ss.getOutputStream(), true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(ss.getInputStream()));
			// ////////////////////////////////////////////////////////////////////////
			// Recibe HOLA.
			// En caso de error de formato, cierra la conexion.
			// ////////////////////////////////////////////////////////////////////////
			
			String linea = read(reader);
			if (!linea.equals(HOLA)) {
				write(writer, ERROR_FORMATO);
				throw new FontFormatException(linea);
			}
			
			//TODO Deberia enviar lista con nombres de archivos divididos por ';'
			write(writer, OK);
			
			System.out.println("Thread " + id + "Terminando\n");
			
		} catch (NullPointerException e) {
			// Probablemente la conexion fue interrumpida.
			printError(e);
		} catch (IOException e) {
			// Error en la conexion con el cliente.
			printError(e);
		} catch (FontFormatException e) {
			// Si hubo errores en el protocolo por parte del cliente.
			printError(e);
		} catch (IllegalStateException e) {
			// El certificado no se pudo generar.
			// No deberia alcanzarce en condiciones normales de ejecuci��n.
			printError(e);
		} // catch (CertificateNotYetValidException e) {
			// El certificado del cliente no se pudo recuperar.
			// El cliente deberia revisar la creacion y envio de su
			// certificado.
		//	printError(e);
	//	} 
	
		 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			try {
				ss.close();
			} catch (Exception e) {
				// DO NOTHING
			}
		}
	}


}
