package labredes.interfazCliente;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import labredes.cliente.Cliente;

public class InterfazCliente extends JFrame implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelArchivos panelArchivos;
	private PanelArchivosDescargados panelArchivosDescargados;
	private PanelConexion panelConexion;
	private Cliente cliente;
	
	public InterfazCliente()
	{
		cliente = new Cliente();
		cliente.addObserver(this);
		setLayout(new BorderLayout());
		JPanel archivos = new JPanel();
		archivos.setLayout(new GridLayout(2,1));
		setSize(600, 250);
		setResizable(false);
		setTitle("Cliente");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		panelArchivos = new PanelArchivos(this);
		archivos.add(panelArchivos);
		
		panelArchivosDescargados = new PanelArchivosDescargados(this);
		archivos.add(panelArchivosDescargados);
		
		add(archivos, BorderLayout.CENTER);
		
		panelConexion = new PanelConexion(this);
		add(panelConexion, BorderLayout.SOUTH);
		panelConexion.actualizar(cliente.estadoConexion());
		
	}
	

	public void abrir(String archivoSeleccionado) {
		// TODO Auto-generated method stub
		cliente.abrirArchivo(archivoSeleccionado);
	}

	public void descargar(String archivoSeleccionado) {
		// TODO Auto-generated method stub
		try{
		if(cliente.paqueteActual()== 0){
			System.out.println("Empieza descarga");
		cliente.empezarDescarga(archivoSeleccionado);
		}else{
			cliente.retomarDescarga(archivoSeleccionado);
		}
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(), "Error al empezar/continuar la descarga", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	

	public void pausar() {
		// TODO Auto-generated method stub
		try {
			cliente.detenerDescarga();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: "+e.getMessage(), "Error al detener la descarga", JOptionPane.ERROR_MESSAGE);
		}
	}

	
	public void establecerConexion()
	{
		try{
		cliente.conexion();
		panelConexion.actualizar(cliente.estadoConexion());
		panelArchivos.actualizar(cliente.listaDeArchivos());
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Error: " +e.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
		}
	}
	

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		System.out.println("Cambio");
		panelArchivos.actualizar(cliente.listaDeArchivos());
		panelArchivosDescargados.actualizarArchivos(cliente.listaDeArchivosDescargados());
		panelConexion.actualizar(cliente.estadoConexion()+" Descargando: "+cliente.descargando() );
		if(cliente.paqueteActual()==0){
			panelArchivos.habilitarNuevaDescarga();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InterfazCliente cliente = new InterfazCliente();		
		cliente.setVisible(true);	
		
	}

}
