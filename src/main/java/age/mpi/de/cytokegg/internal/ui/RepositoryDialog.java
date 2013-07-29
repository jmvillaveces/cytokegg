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
