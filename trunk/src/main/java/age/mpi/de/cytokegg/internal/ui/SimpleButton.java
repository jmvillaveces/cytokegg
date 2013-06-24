package age.mpi.de.cytokegg.internal.ui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class SimpleButton extends JButton {
	
	public SimpleButton(){
		super();
		
		setForeground(Color.BLACK);
		setBackground(Color.WHITE);
		setBorderPainted(false);
		setHorizontalAlignment(SwingConstants.LEFT);
	}
	
	public SimpleButton(ImageIcon icon){
		super(icon);
		
		setForeground(Color.BLACK);
		setBackground(Color.WHITE);
		setBorderPainted(false);
		setHorizontalAlignment(SwingConstants.LEFT);
	}
	
	public SimpleButton(String text, ImageIcon icon){
		super(text, icon);
		
		setForeground(Color.BLACK);
		setBackground(Color.WHITE);
		setBorderPainted(false);
		setHorizontalAlignment(SwingConstants.LEFT);		
	}
}
