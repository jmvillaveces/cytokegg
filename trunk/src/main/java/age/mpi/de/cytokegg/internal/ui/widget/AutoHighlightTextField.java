/**
 * Copyright 2013 José María Villaveces Max Planck institute for biology of
 * ageing (MPI-age)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
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