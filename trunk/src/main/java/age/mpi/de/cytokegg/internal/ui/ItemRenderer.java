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

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import age.mpi.de.cytokegg.internal.util.Item;
import age.mpi.de.cytokegg.internal.util.PathwayItem;

public class ItemRenderer extends BasicComboBoxRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index,boolean isSelected, boolean cellHasFocus){
    	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null || index == -1){
        	if(value instanceof  Item){
        		Item item = (Item)value;
                setText(item.getDescription());
            }else if(value instanceof  PathwayItem){
            	PathwayItem item = (PathwayItem)value;
                setText(item.toString());
            }
        }
        return this;
    }
}
