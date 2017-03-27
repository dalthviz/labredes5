package labredes.interfazCliente;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class PanelArchivosDescargados extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private InterfazCliente principal;
	private JComboBox archivos;
	private String archivoSeleccionado;
	
	public PanelArchivosDescargados(InterfazCliente interfaz) {
		// TODO Auto-generated constructor stub
			
		principal = interfaz;
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Archivos descargados"));
		//Solo para probar abrir un archivo
		String[] descarga = new String[1];
		descarga[0] = "descarga.txt";
		archivos = new JComboBox(descarga);
		archivos.addActionListener(this);
		
		JPanel seleccion = new JPanel();
		seleccion.setLayout(new GridLayout(1, 2));
		seleccion.add(new Label("Archivos para abrir: "));
		seleccion.add(archivos);
		add(seleccion, BorderLayout.CENTER);
		
		JButton abrir = new JButton("Abrir");
		abrir.addActionListener(this);
		abrir.setActionCommand("ABRIR");
		add(abrir, BorderLayout.SOUTH);
				
	}

	public void actualizarArchivos(ArrayList archivosDescargados)
	{
		archivos = new JComboBox(archivosDescargados.toArray());
		repaint();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String comando = e.getActionCommand();
		if(comando.equals("ABRIR") && archivoSeleccionado != null){
			principal.abrir(archivoSeleccionado);
		}
		else if(!comando.equals("ABRIR") ){
			JComboBox cb = (JComboBox)e.getSource();
		    archivoSeleccionado = (String)cb.getSelectedItem();
		    
		}
		else{
			JOptionPane.showMessageDialog(this, "Seleccione un archivo descargado");
		}
	}

}
