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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import org.apache.lucene.queryParser.ParseException;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.kegg.rest.KeggService;
import org.cytoscape.cytokegg.task.IndexBuilderTask;
import org.cytoscape.cytokegg.ui.widget.AutoHighlightTextField;
import org.cytoscape.cytokegg.util.Item;
import org.cytoscape.cytokegg.icons.IconLoader;

import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class AddOrgPanel extends JFrame {
	
	private Item[] orgs;
	private JList list;
	private AutoHighlightTextField searchField;
	private JButton add;
	private RepositoryForm repositoryForm;
	
	
	public AddOrgPanel(RepositoryForm repositoryForm) throws RemoteException{
		
		this.repositoryForm = repositoryForm;
		
		//Organisms
		orgs = KeggService.getInstance().getOrganisms();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder("Add Organism"));
		
		//Add Button
		add = new JButton(IconLoader.getInstance().getDatabaseAddIcon());
		add.setToolTipText("Add organism to index");
		add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Item i = (Item) list.getSelectedValue();
				
				if(i != null){
	            	try{
	            		if(Repository.getInstance().isOrganismIndexed(i.getId())){
	            			int answer = JOptionPane.showConfirmDialog(null, "It seems like "+i.getDescription()+" has been indexed already.\n"
	                				+"Do you want to reindex it ?");
	                		
	            			if(answer == JOptionPane.YES_OPTION){
	            				Repository.getInstance().deleteOrg(i.getId());
	            				index(i);
	                		}
	                	}else{
	                		index(i);
	                	}
	            	}catch(Exception e){
	                	JOptionPane.showMessageDialog(null, "Error while indexing the data.");
	            	}
            	}
			}
		});
		
		//Organisms jlist
		list = new JList();
		list.setCellRenderer(new ItemRenderer());
        list.setListData(orgs);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.clearSelection();
        
        //Search Field
        searchField = new AutoHighlightTextField(20);
        //searchField.sets
        searchField.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				AddOrgPanel.this.filter(AddOrgPanel.this.searchField.getText());
			}
        	
        });
        
        {
        	JPanel aux = new JPanel();
        	aux.add(searchField);
        	aux.add(add);
        	panel.add(aux, BorderLayout.NORTH);
        }
        
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        
        setContentPane(panel);
	}
	
	private void index(Item item) throws IOException, ParseException{
		Task task = new IndexBuilderTask(new Item[]{item});
        JTaskConfig config = new JTaskConfig();
        config.displayCancelButton(false);
        config.displayStatus(true);
        config.displayTimeElapsed(true);
        
        boolean success = TaskManager.executeTask(task, config);
        if(!success){
        	JOptionPane.showMessageDialog(null, "Error while indexing the data.");
        }
        repositoryForm.refresh();
	}
	
	private void filter(String str){
			DefaultListModel model = new DefaultListModel();
			for(int i=0; i<orgs.length; i++){
				if(orgs[i].getDescription().toLowerCase().startsWith(str.toLowerCase())){
					model.addElement(orgs[i]);
				}
			}
			list.setModel(model);
	}
}
