package de.mpg.biochem.cytokegg.internal.ui.widget;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;


public class AutoHighlightTextField extends JTextField implements FocusListener{
	
	public AutoHighlightTextField(int cols){
		super(cols);
		this.addFocusListener(this);
		
		// Make this look like a normal search field on OS X.
		// Note that the field MUST NOT be forced to a height other than its
		// preferred height; that produces some ugly visual glitches.
		this.putClientProperty( "JTextField.variant", "search" );
	}
	
	public AutoHighlightTextField(){
		super();
		this.addFocusListener(this);
		
		// Make this look like a normal search field on OS X.
		// Note that the field MUST NOT be forced to a height other than its
		// preferred height; that produces some ugly visual glitches.
		this.putClientProperty( "JTextField.variant", "search" );
	}

	public AutoHighlightTextField( final String text ){
		super(text);
		this.addFocusListener(this);
		
		// Make this look like a normal search field on OS X.
		// Note that the field MUST NOT be forced to a height other than its
		// preferred height; that produces some ugly visual glitches.
		this.putClientProperty( "JTextField.variant", "search" );
	}

	public void setText( final String text ){
		super.setText( text );
		this.selectAll();
	}

	public void focusGained( final FocusEvent e ){
		this.selectAll();
	}

	public void focusLost( final FocusEvent e ){
	}

	public void selectAll(){
			super.selectAll();
	}
}