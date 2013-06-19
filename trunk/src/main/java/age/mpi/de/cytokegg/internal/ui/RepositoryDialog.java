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
package age.mpi.de.cytokegg.internal.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.cytoscape.work.TaskIterator;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.task.IndexBuilderTask;
import age.mpi.de.cytokegg.internal.util.Item;
import age.mpi.de.cytokegg.internal.util.PluginProperties;

public class RepositoryDialog extends JDialog implements Updatable {
	
	private JList list;
	private JButton rebuild, add;
	private AddOrgDialog addOrg;
	
	public RepositoryDialog(JFrame owner) throws Exception{
		super(owner, "Repository", false);
		
		setLayout(new BorderLayout());
		
		JPanel panel = new JPanel(new BorderLayout());
		//panel.setBorder(new TitledBorder("Repository"));
		
		setSize(new Dimension(400, 400));
		
		//North Panel
		{
			//rebuild button
			rebuild = new JButton("Reindex");
			rebuild.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent actionEvent) {
	            	try {
	            		CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(new IndexBuilderTask(PluginProperties.getInstance().getDefaultOrganisms())));
	        		} catch (IOException e) {
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
			//update();
	       
		}
		
		JPanel aux = new JPanel();
		aux.add(rebuild);
		aux.add(add);
		
		panel.add(aux, BorderLayout.NORTH);
		
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		setContentPane(panel);
		
	}
	
	private void add(){
		if(addOrg == null){
    		try {
				addOrg = new AddOrgDialog(this);
				addOrg.setSize(new Dimension(400, 400));
				addOrg.setVisible(true);
			} catch (RemoteException e) {
				e.printStackTrace();
			} 
    	}else{
    		addOrg.setVisible(true);
    	}
	}

	@Override
	public void update() throws Exception {
		DefaultListModel model = (DefaultListModel) list.getModel();
		model.clear();
		Item[] orgs = Repository.getInstance().getIndexedOrganisms();
		if(orgs != null){
			for(Item i : orgs){
				model.addElement(i);
			}
			list.clearSelection();
		}
	}
}
