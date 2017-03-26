package labredes.cliente;
import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;


public class Cliente extends Observable{

	public final static int SOCKET_PORT = 4444;      //TODO deberia ser igual que el servidor
	public final static String SERVER = "127.0.0.1";  // localhost
	public final static String
	FILE_TO_RECEIVED_PATH = "./data/";  // path donde se descargan lo archivos

	//COnstantes de comunicación
	public final static String HOLA = "HOLA";
	public final static String ENVIAR = "ENVIAR";
	public final static String ENVIANDO = "ENVIANDO";
	public final static String PARAR = "PARAR";
	public final static String OK = "OK";
	public final static String PARANDO_TRANSFERENCIA = "PARANDO_TRANSFERENCIA";

	//Atributos de conexión
	private Socket socket;
	private InputStream inS;
	private OutputStream outS;
	private BufferedReader in;
	private PrintWriter out;
	
	//Atributos para descarga archivo
	private int bytesRead;
	private byte[] mybytearray;
	private int current = 0;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private String[] listaDeArchivos = null;
	private ArrayList<String> listaDeArchivosDescargados = new ArrayList<String>();
	private double tamPaquetes;
	private int numPaquetes;
	private int paqueteActual;
	private int buffer_size;
	private boolean corriendo = false;
	private boolean descargando = false;
	

	public Cliente() {

	}

	public synchronized String[] listaDeArchivos (){
		return listaDeArchivos;
	}

	public synchronized ArrayList<String> listaDeArchivosDescargados()
	{
		return listaDeArchivosDescargados;
	}

	public synchronized boolean descargando (){
		return descargando;
	}

	public synchronized boolean corriendo()
	{
		return corriendo;
	}
	
	public synchronized int paqueteActual(){
		return paqueteActual;
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

	public void empezarDescarga(String archivo) throws Exception
	{
		descargando = true;
		String file_path = FILE_TO_RECEIVED_PATH + archivo;
		// receive file
		sendMessageToServer(archivo);
		String[] infoEnvio = waitForMessageFromServer().split(";");
		buffer_size = Integer.parseInt(infoEnvio[0]);
		tamPaquetes = Double.parseDouble(infoEnvio[1]);
		numPaquetes = Integer.parseInt(infoEnvio[2]);
		paqueteActual = 1;

		sendMessageToServer(ENVIAR);
		String respuesta = waitForMessageFromServer();

		if(respuesta.contains(ENVIANDO)){
			descargando = true;
			mybytearray  = new byte [buffer_size];
			try {
				
				fos = new FileOutputStream(file_path);
				bos = new BufferedOutputStream(fos);
				bytesRead = inS.read(mybytearray,0,mybytearray.length);
				current = bytesRead;
				bos.write(mybytearray, 0 , current);
				do {
					bytesRead = inS.read(mybytearray, 0, mybytearray.length);
					bos.write(mybytearray, 0 , bytesRead);
					if(bytesRead >= 0) current += bytesRead;
					System.out.println(current + " bytes read");
				} while(bytesRead > -1);	
				paqueteActual++;
				System.out.println("File " + file_path + " downloaded (" + current + " bytes read)");
				runDescarga(archivo);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					if (fos != null)
						fos.close();
					if (bos != null) bos.close();
				} catch (IOException a) {
					// TODO Auto-generated catch block
					a.printStackTrace();
				}
			}
		}
		else{
			System.out.println(respuesta);
			throw new Exception("Respuesta inesperada: "+respuesta);
		}

	}

	public void recibirPaquete(String archivo){
		
		String file_path = FILE_TO_RECEIVED_PATH + archivo;
		
		sendMessageToServer(ENVIAR+":PAQUETE"+paqueteActual+1);
		String respuesta = waitForMessageFromServer();
		if(respuesta.contains(ENVIANDO)){
		try {
			bytesRead = inS.read(mybytearray,0,mybytearray.length);
			if(bytesRead > -1){
			current += bytesRead;
			bos.write(mybytearray, 0 , current);
//			bos.flush();			
			do {
				bytesRead = inS.read(mybytearray, 0, mybytearray.length);
				bos.write(mybytearray, 0 , bytesRead);
				if(bytesRead >= 0) current += bytesRead;
				System.out.println(current + " bytes read");
			} while(bytesRead > -1);				
			paqueteActual++;
			}
			else{
				sendMessageToServer(OK);
				descargando = false;
				corriendo = false;
				paqueteActual = 0;
			}
			System.out.println("File " + file_path + " downloaded (" + current + " bytes read)");
			
		} catch (IOException e) {
			e.printStackTrace();			
			try {
				if (fos != null) fos.close();
				if (bos != null) bos.close();
			} catch (IOException a) {
				a.printStackTrace();
			}
		}
		}
	}

	public synchronized void detenerDescarga() throws Exception
	{
		descargando = false;
		sendMessageToServer(PARAR);
		String respuesta = waitForMessageFromServer();
		if(respuesta.contains(PARANDO_TRANSFERENCIA)){
			assert "PAQUETE"+(paqueteActual+1) == respuesta.split(":")[1];
		}else{
			throw new Exception("No se recibio respuesta con #paquete de siguiente antes de parada");
		}
	}

	public void retomarDescarga(String archivoSeleccionado) {
		descargando = true;
		runDescarga(archivoSeleccionado);
	}


	public synchronized String estadoConexion()
	{
		String estado = "No se ha establecido una conexión";
		if (socket != null){
			return socket.isConnected() && !socket.isClosed()? "Conexión Activa": "Conexión Cerrada";
		}
		return estado;
	}

	public synchronized void conexion() throws UnknownHostException, IOException
	{
		if (socket == null) {

			this.socket = new Socket(SERVER, SOCKET_PORT);
			this.inS = this.socket.getInputStream();
			this.outS = this.socket.getOutputStream();
			this.in = new BufferedReader(new InputStreamReader(this.inS));
			this.out = new PrintWriter(this.outS, true);
			this.descargando = false;

			this.sendMessageToServer(HOLA);
			this.listaDeArchivos = this.waitForMessageFromServer().split(";");

		}
	}

	public void abrirArchivo(String archivo){

		try {
			File objetofile = new File (FILE_TO_RECEIVED_PATH + archivo);
			Desktop.getDesktop().open(objetofile);

		}catch (IOException ex) {

			System.out.println(ex);

		}

	}


	public void runDescarga(String archivo) {
		// TODO Auto-generated method stub
		corriendo = true;
		while(corriendo){
			System.out.println("RUN-Paquete Actual: " + paqueteActual + "\nTamaño: " + tamPaquetes);
			if(descargando())
			{
				recibirPaquete(archivo);
				if(paqueteActual == numPaquetes)
				{
					corriendo = false;
					descargando = false;
					paqueteActual = 0;
					listaDeArchivosDescargados.add(archivo);
					setChanged();
					notifyObservers();
				}
			}
		}
	}
}
