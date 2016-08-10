package de.mpg.biochem.cytokegg.internal.ui;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import de.mpg.biochem.cytokegg.internal.util.Item;


public class ItemRenderer extends BasicComboBoxRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index,boolean isSelected, boolean cellHasFocus){
    	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null || index == -1){
        	if(value instanceof  Item){
        		Item item = (Item)value;
                setText(item.getDescription());
            }/*else if(value instanceof  PathwayItem){
            	PathwayItem item = (PathwayItem)value;
                setText(item.toString());
            }*/
        }
        return this;
    }
}
