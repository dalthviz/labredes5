package labredes.interfazCliente;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class PanelConexion extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton conexion;
	
	private InterfazCliente principal;
	private JLabel estado;
	
	public PanelConexion(InterfazCliente interfaz) {
		// TODO Auto-generated constructor stub
		principal = interfaz;
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Conexión"));
		JPanel opciones = new JPanel(new GridLayout(1, 2));
		estado = new JLabel();
		conexion = new JButton("Establecer Conexion");
		conexion.addActionListener(this);
		opciones.add(estado);
		opciones.add(conexion);
		
		add(opciones, BorderLayout.CENTER);
		
	}
	
	public void actualizar(String estadoActual)
	{
		estado.setText(estadoActual);
		repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		principal.establecerConexion();		
	}

}
