package labredes.interfazCliente;

import javax.swing.JFrame;

import labredes.cliente.Cliente;

public class InterfazCliente extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Cliente cliente;
	public InterfazCliente()
	{
		cliente = new Cliente();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InterfazCliente cliente = new InterfazCliente();
		cliente.setVisible(true);
	}

}
