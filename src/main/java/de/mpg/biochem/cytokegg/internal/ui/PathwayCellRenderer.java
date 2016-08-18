package de.mpg.biochem.cytokegg.internal.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PathwayCellRenderer extends DefaultTableCellRenderer {
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
			boolean hasFocus, int row, int column) {
		
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		String msg = (value == null) ? "" : value.toString();
		this.setToolTipText(msg);
		
		return this;
		
	}
	
	@Override
	public void setToolTipText(String msg) {
		super.setToolTipText("<html><body><div style='width: 300px; text-justification: justify;'>" + msg + "</div></body></html>");
	}
}
