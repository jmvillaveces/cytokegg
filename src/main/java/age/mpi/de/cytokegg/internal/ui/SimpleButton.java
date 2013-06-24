/*
 * Copyright (C) 2011-2012 José María Villaveces Max Planck institute for
 * biology of ageing (MPI-age)
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
