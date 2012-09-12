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
package org.cytoscape.cytokegg.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.task.IndexBuilderTask;
import org.cytoscape.cytokegg.util.Item;
import org.cytoscape.cytokegg.util.PluginProperties;
import org.jdesktop.swingx.VerticalLayout;

import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class RepositoryForm extends JFrame {
	
	private JList list;
	private JButton rebuild, add;
	private AddOrgPanel addOrg;
	
	public RepositoryForm() throws CorruptIndexException, ParseException, IOException{
		setLayout(new BorderLayout());
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder("Repository"));
		
		//North Panel
		{
			//rebuild button
			rebuild = new JButton("Reindex");
			rebuild.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent actionEvent) {
	            	try{
	            		Task task = new IndexBuilderTask(PluginProperties.getInstance().getDefaultOrganisms());
		                JTaskConfig config = new JTaskConfig();
		                config.displayCancelButton(false);
		                config.displayStatus(true);
		                config.displayTimeElapsed(true);
		                
		                Repository.getInstance().deleteIndex();
		                
		                boolean success = TaskManager.executeTask(task, config);
		                if(!success){
		                	Repository.getInstance().deleteIndex();
		                	JOptionPane.showMessageDialog(null, "Error while indexing the data. The index has been deleted!");
		                }else{
			                refresh();
		                }
	            	}catch(Exception e){
	            		try {
							Repository.getInstance().deleteIndex();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
	                	JOptionPane.showMessageDialog(null, "Error while indexing the data. The index has been deleted!");
	                	e.printStackTrace();
	            	}
	        	}
	        });
			
			//add button
			add = new JButton("Add organism");
			add.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent actionEvent) {
	            	add();
	        	}
	        });
		}
		
		//Central Panel
		{
			//in list
			list = new JList();
			list.setCellRenderer(new ItemRenderer());
	        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        list.setModel(new DefaultListModel());
			refresh();
	       
		}
		
		JPanel aux = new JPanel();
		aux.add(rebuild);
		aux.add(add);
		
		panel.add(aux, BorderLayout.NORTH);
		
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		setContentPane(panel);
		
	}
	
	public void refresh() throws CorruptIndexException, ParseException, IOException{
		DefaultListModel model = (DefaultListModel) list.getModel();
		model.clear();
		for(Item i : Repository.getInstance().getIndexedOrganisms()){
			model.addElement(i);
		}
		list.clearSelection();
	}
	
	private void add(){
		if(addOrg == null){
    		try {
				addOrg = new AddOrgPanel(this);
				addOrg.setSize(new Dimension(400, 400));
				addOrg.setVisible(true);
			} catch (RemoteException e) {
				e.printStackTrace();
			} 
    	}else{
    		addOrg.setVisible(true);
    	}
	}
}
