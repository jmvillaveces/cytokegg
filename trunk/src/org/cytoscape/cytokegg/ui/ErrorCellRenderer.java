package org.cytoscape.cytokegg.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class ErrorCellRenderer  extends DefaultTableCellRenderer{
	
	private Border unselectedBorder = null, selectedBorder = null;
	
	public ErrorCellRenderer(){
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if(column > 0){
			try{
				Double.parseDouble(value.toString());
				if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,table.getBackground());
                }
                //setBorder(unselectedBorder);
				this.setBackground(table.getBackground());
			}catch(NumberFormatException e){
				if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,table.getSelectionBackground());
                }
				//setBorder(selectedBorder);
				this.setBackground(Color.red);
			}
		}
		return this;
	}
}
