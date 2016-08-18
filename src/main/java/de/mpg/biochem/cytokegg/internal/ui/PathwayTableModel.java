package de.mpg.biochem.cytokegg.internal.ui;

import java.util.Iterator;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import de.mpg.biochem.cytokegg.internal.Pathway;

public class PathwayTableModel extends AbstractTableModel {

	private String columnNames[] = { "Name", "Genes in network", "Description" };
	private Object[][] data = new Object[0] [3];
	
	public PathwayTableModel(){
	}
	
	public void setData(Set<Pathway> pathways){
		
		data = new Object[pathways.size()] [3];
		
		int i = 0;
		Iterator<Pathway> iterator = pathways.iterator();
		while(iterator.hasNext()){
			Pathway path = iterator.next();
			data[i] = new String[] { path.getName(), path.getGenesInNetwork().size() + "/" + path.getGenes().size(),  path.getDescription()};
			i++;
		}
		
		this.fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
}
