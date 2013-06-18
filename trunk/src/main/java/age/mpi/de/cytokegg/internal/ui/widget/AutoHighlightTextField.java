/*
 * Copyright (C) 2011-2012 José María Villaveces Max Plank institute for biology
 * of ageing (MPI-age)
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package age.mpi.de.cytokegg.internal.ui.widget;

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