package labredes.cliente;

public class Descarga extends Thread {

	private Cliente cliente;
	
	public Descarga (Cliente c){
		cliente = c;
	}
	
	public void run()
	{
		// TODO Auto-generated method stub
		cliente.corriendo = true;
				while(cliente.corriendo()){
					System.out.println("RUN-Paquete Actual: " + cliente.paqueteActual + "\nTamaño: " + cliente.tamPaquetes);
					if(cliente.descargando())
					{
						
						cliente.recibirPaquete();
						if(cliente.paqueteActual == cliente.numPaquetes)
						{
							cliente.corriendo = false;
							cliente.descargando = false;
							cliente.paqueteActual = 0;
							cliente.listaDeArchivosDescargados.add(cliente.archivoDescarga);
							cliente.cambio();
						}
					}
				}
	}
	
}
