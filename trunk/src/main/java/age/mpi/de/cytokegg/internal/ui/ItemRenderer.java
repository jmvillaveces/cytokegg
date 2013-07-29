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
