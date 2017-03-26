package labredes.interfazCliente;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import javafx.scene.layout.Border;

public class PanelArchivos extends JPanel implements ActionListener{

	private InterfazCliente principal;
	private JComboBox archivos;
	private String archivoSeleccionado;
	private JButton descargar;
	private JButton pausar;
	
	
	public PanelArchivos(InterfazCliente interfaz) {
		// TODO Auto-generated constructor stub
		principal = interfaz;
		setLayout(new GridLayout(2, 2));
		//Solo para probar abrir un archivo
		String[] descarga = new String[1];
		descarga[0] = "descarga.txt";
		archivos = new JComboBox(descarga);
		archivos.addActionListener(this);
		setBorder(new TitledBorder("Archivos disponibles"));
		
		
		JPanel seleccion = new JPanel();
		seleccion.setLayout(new GridLayout(1, 2));
		seleccion.add(new Label("Archivos para descargar"));
		seleccion.add(archivos);
		
		add(seleccion, BorderLayout.CENTER);
		
		descargar = new JButton("Descargar");
		descargar.addActionListener(this);
		descargar.setActionCommand("DESCARGAR");
		descargar.setEnabled(false);
		
		pausar = new JButton("Pausar Descarga");
		pausar.addActionListener(this);
		pausar.setActionCommand("PAUSAR");
		pausar.setEnabled(false);
		
		JPanel botones = new JPanel();
		botones.setLayout(new GridLayout(1, 2));
		botones.add(descargar);
		botones.add(pausar);
		
		add(botones, BorderLayout.SOUTH);
				
	}

	public void actualizar(String[] listaArchivos){
		archivos = new JComboBox(listaArchivos);
	}
	
	public void habilitarNuevaDescarga()
	{
		archivos.setEnabled(true);
		descargar.setEnabled(true);
		descargar.setText("Descargar");
		pausar.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String comando = e.getActionCommand();
		if(comando.equals("DESCARGAR") && archivoSeleccionado != null){
			principal.descargar(archivoSeleccionado);
			archivos.setEnabled(false);
			descargar.setEnabled(false);
			pausar.setEnabled(true);
		}
		else if(comando.equals("PAUSAR") ){
		    pausar.setEnabled(false);
			descargar.setEnabled(true);
			descargar.setText("Continuar");
			principal.pausar();
		}
		else if(!comando.equals("DESCARGAR") && !comando.equals("PAUSAR")){

			JComboBox cb = (JComboBox)e.getSource();
		    archivoSeleccionado = (String)cb.getSelectedItem();
		    descargar.setEnabled(true);
		    pausar.setEnabled(false);
		}
		else{
			JOptionPane.showMessageDialog(this, "Seleccione un archivo a descargar");
		}
	    
	}
	
	
}
