package labredes.worker;

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

import javafx.scene.control.Separator;
import labredes.servidor.Servidor;


/**
 * Esta clase implementa el protocolo que se realiza al recibir una conexiÃ³n de un cliente.
 * Infraestructura Computacional Universidad de los Andes. 
 * Las tildes han sido eliminadas por cuestiones de compatibilidad.
 * 
 * @author Cristian FabiÃ¡n Brochero 		-  201620.
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
	public static final String PARANDO_TRANSFERENCIA = "PARANDO_TRANSFERENCIA";
	public static final String ARCHIVOS = "5MB.txt:20MB.txt:50MB.txt";
	public static final String ARCHIVO = "ARCHIVO";
	public static final String SEPARADOR = ":";
	public static final String INIT = "INIT";
	public static final String RTA = "RTA";
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";
	public static final String ERROR_FORMATO = "Error en el formato. Cerrando conexion";
	public final static String HOLA = "HOLA";
	public final static String ENVIAR = "ENVIAR";
	public final static String ENVIANDO = "ENVIANDO";
	public final static String PARAR = "PARAR";
	public final static String PAQUETE = "PAQUETE";
	
	private int id;
	private Socket ss;
	private int archivo = -1;
	private int numPackets;
	private File myFile = null;
	private PrintWriter writer = null;
	private BufferedReader reader = null;
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private OutputStream os = null;
	private byte[] mybytearray = new byte[Servidor.BUFFER_SIZE];
	
	
	public Worker(int pId, Socket pSocket) {
		id = pId;
		ss = pSocket;
	}
	
	/**
	 * Metodo que se encarga de imprimir en consola todos los errores que se 
	 * producen durante la ejecuaciÃ³n del protocolo. 
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
	

	public void envioPaquete(int numPaquete){
				
		 try {
                          
             bis.read(mybytearray,Servidor.PACKET_SIZE*numPaquete,Servidor.PACKET_SIZE);
             os = ss.getOutputStream();
             System.out.println("Sending " +Servidor.FILE_TO_SEND+ARCHIVOS.split(SEPARADOR)[archivo] + "(" + mybytearray.length + " bytes)");
             os.write(mybytearray,0,Servidor.PACKET_SIZE);
             os.flush();	              
             
             System.out.println("Done paquete "+numPaquete); 
       	  
         }
	  catch(Exception e){
		  try {
				if (bis != null)bis.close();
		         if (os != null) os.close();
			} catch (IOException a) {
				// TODO Auto-generated catch block
				a.printStackTrace();
				System.out.println("Envio paquete");
			}
	  }       

 }
	
	  public void comienzoEnvio() throws IOException {
		
	        try {
	                  
	              mybytearray  = new byte [(int)myFile.length()];
	              fis = new FileInputStream(myFile);
	              bis = new BufferedInputStream(fis);
	              
	              bis.read(mybytearray,0,Servidor.PACKET_SIZE);
	              os = ss.getOutputStream();
	              System.out.println("Sending " +Servidor.FILE_TO_SEND+ARCHIVOS.split(SEPARADOR)[archivo] + "(" + mybytearray.length + " bytes)");
	              os.write(mybytearray,0,Servidor.PACKET_SIZE);
	              os.flush();	              
	              
	              System.out.println("Done paquete 1"); 
	        	  
	          }
		  catch(Exception e){
			  if (bis != null) bis.close();
	          if (os != null) os.close();	        
		  }          

	  }
	  
	public synchronized int getNumPackets() {
			return numPackets;
	}
	
	/**
	 * Metodo que establece el protocolo de comunicacion con el punto de atencion.
	  */

	public void run(){
		try{			
			writer = new PrintWriter(ss.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(ss.getInputStream()));
			// ////////////////////////////////////////////////////////////////////////
			// Recibe HOLA.
			// En caso de error de formato, cierra la conexion.
			// ////////////////////////////////////////////////////////////////////////
			
			String linea = read(reader);
			if (!linea.equals(HOLA)) {
				write(writer, ERROR_FORMATO);
				throw new IllegalArgumentException(linea);
			}
			
			//TODO Deberia enviar lista con nombres de archivos divididos por ';' los archivos se deberian encontrar en ./serverData
			write(writer, ARCHIVOS);
			linea = read(reader); //Queda esperando por petición de un archivo
			if(!linea.equals(ARCHIVOS.split(SEPARADOR)[0])&&!linea.equals(ARCHIVOS.split(SEPARADOR)[1])
					&&!linea.equals(ARCHIVOS.split(SEPARADOR)[2])){
				write(writer, ERROR_FORMATO);
				System.out.println("ENTRA EN ERROR");
				throw new IllegalArgumentException(linea);				
			}
			//TODO Enviar información de archivo seleccionado
			else
			{
				if (linea.equals(ARCHIVOS.split(SEPARADOR)[0])) archivo = 0;
				else if (linea.equals(ARCHIVOS.split(SEPARADOR)[1])) archivo = 1;
				else archivo = 2;
				int numPaquetes = 0;
				myFile = new File (Servidor.FILE_TO_SEND+ARCHIVOS.split(SEPARADOR)[archivo]);
				long tamFile = myFile.length();
				numPaquetes = (int)(tamFile/Servidor.PACKET_SIZE);
				if((numPaquetes%Servidor.PACKET_SIZE) == 0)numPackets = numPaquetes;
				else numPackets = numPaquetes+1;
			}
			//TODO Enviar información de archivo seleccionado
			write(writer, Servidor.BUFFER_SIZE + SEPARADOR + Servidor.PACKET_SIZE + SEPARADOR + getNumPackets());
			
			linea = read(reader); //Queda esperando por petición de un archivo
			if(!linea.equals(ENVIAR)){
				write(writer, ERROR_FORMATO);
				throw new IllegalArgumentException(linea);				
			}
			//Servidor -> "ENVIANDO:PAQUETE1"
			comienzoEnvio();
			
			linea = read(reader);
			if(linea.contains(ENVIAR)){//Cliente -> "ENVIAR"
				String[] enviarPaquete = linea.split(":");
				String paquete = enviarPaquete[1];
				int numPaquete = Integer.parseInt(paquete.replace("PAQUETE", ""));
				
				envioPaquete(numPaquete);	
								
				while(!(linea=read(reader)).equals(OK)){}
					if(linea.contains(ENVIAR))
					{
						enviarPaquete = linea.split(":");
						paquete = enviarPaquete[1];
						numPaquete = Integer.parseInt(paquete.replace("PAQUETE", ""));						
						envioPaquete(numPaquete);	
					}else if(linea.equals(PARAR))
					{
						write(writer, PARANDO_TRANSFERENCIA);
						linea = read(reader);
					}
					else{
						throw new Exception("Mensaje inesperado");
					}
				}
			
			System.out.println("Thread " + id + "Terminando\n");
		} catch (NullPointerException e) {
			// Probablemente la conexion fue interrumpida.
			printError(e);
		} catch (IOException e) {
			// Error en la conexion con el cliente.
			printError(e);
		} catch (IllegalArgumentException e) {
			// Si hubo errores en el protocolo por parte del cliente.
			printError(e);
		} catch (IllegalStateException e) {
			// El certificado no se pudo generar.
			// No deberia alcanzarce en condiciones normales de ejecuciï¿½ï¿½n.
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
