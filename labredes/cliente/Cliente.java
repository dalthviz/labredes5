package labredes.cliente;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Cliente {

	public final static int SOCKET_PORT = 4444;      // you may change this
	public final static String SERVER = "127.0.0.1";  // localhost
	public final static String
	FILE_TO_RECEIVED = "C:/Users/Daniel/Desktop/Redes/Laboratorio 5/labredes5/README4.md";  // you may change this, I give a
	// different name because i don't want to
	// overwrite the one used by server...

	public final static int FILE_SIZE = 6022386; // file size temporary hard coded
	// should bigger than the file to be downloaded
	//Servidor debe de especificar tamaño archivo antes de descarga

	public final static String HOLA = "HOLA";
	
	private Socket socket;
	private InputStream inS;
	private OutputStream outS;
	private BufferedReader in;
	private PrintWriter out;
	private boolean descargando = false;
	private int bytesRead;
	private int current = 0;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private String[] listaDeArchivos = null;

	public Cliente() {

	}

	public String[] listaDeArchivos (){
		return listaDeArchivos;
	}
	
	public boolean descargando (){
		return descargando;
	}
	
	public synchronized void sendMessageToServer(String message) {
		this.out.println(message);
	}

	public synchronized String waitForMessageFromServer() {
		try {
			String answer = this.in.readLine();
			System.out.println("Client - Message: " + answer);
			return answer;
		}
		catch (IOException e) {
			System.out.println("Fail to Listen ACK from Server: " + e.getMessage());
			return null;
		}
	}

	public void startDownload(String archivo)
	{
		descargando = true;
		// receive file
		byte [] mybytearray  = new byte [FILE_SIZE];
		InputStream is;
		try {
			is = socket.getInputStream();
			fos = new FileOutputStream(FILE_TO_RECEIVED);
			bos = new BufferedOutputStream(fos);
			bytesRead = is.read(mybytearray,0,mybytearray.length);
			current = bytesRead;

			do {
				bytesRead =
						is.read(mybytearray, current, (mybytearray.length-current));
				if(bytesRead >= 0) current += bytesRead;
				System.out.println(current + " bytes read");
			} while(bytesRead > -1);

			bos.write(mybytearray, 0 , current);
			bos.flush();
			System.out.println("File " + FILE_TO_RECEIVED
					+ " downloaded (" + current + " bytes read)");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {			
				try {
					if (fos != null)
					fos.close();
					if (bos != null) bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}


	}

	public synchronized void stopDownload()
	{

	}

	public String estadoConexion()
	{
		String estado = "No se ha establecido una conexión";
		if (socket != null){
		return socket.isConnected() && !socket.isClosed()? "Conexión Activa": "Conexión Cerrada";
		}
		return estado;
	}
	
	public void conexion()
	{
		if (socket == null) {
			try {
				this.socket = new Socket(SERVER, SOCKET_PORT);
				this.inS = this.socket.getInputStream();
				this.outS = this.socket.getOutputStream();
				this.in = new BufferedReader(new InputStreamReader(this.inS));
				this.out = new PrintWriter(this.outS, true);
				this.descargando = false;
				
				this.sendMessageToServer(HOLA);
				this.listaDeArchivos = this.waitForMessageFromServer().split(";");
			}
			catch (Exception e) {
				System.out.println("Fail Opening de Client Socket: " + e.getMessage());
			}

		}
	}
}
