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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.lucene.queryparser.classic.ParseException;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.service.KeggService;
import age.mpi.de.cytokegg.internal.task.IndexBuilderTask;
import age.mpi.de.cytokegg.internal.ui.widget.AutoHighlightTextField;
import age.mpi.de.cytokegg.internal.util.IconLoader;
import age.mpi.de.cytokegg.internal.util.Item;

public class AddOrgDialog extends JDialog {
	
	private Item[] orgs;
	private JList list;
	private AutoHighlightTextField searchField;
	private JButton add;
	private RepositoryDialog repositoryForm;
	
	public AddOrgDialog(RepositoryDialog repositoryForm) throws RemoteException{
		super(repositoryForm, "Add Organism", true);
		
		this.repositoryForm = repositoryForm;
		
		//Organisms
		orgs = KeggService.getInstance().getOrganisms();
		
		JPanel panel = new JPanel(new BorderLayout());
		//panel.setBorder(new TitledBorder("Add Organism"));
		
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
				AddOrgDialog.this.filter(AddOrgDialog.this.searchField.getText());
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
	
	private void index(final Item item) throws IOException, ParseException{
		
		IndexBuilderTask task = new IndexBuilderTask(new Item[]{item});
		Task utilTask = new AbstractTask(){

			@Override
			public void run(TaskMonitor taskMonitor) throws Exception {
				// Give the task a title.
		        taskMonitor.setTitle("Refreshing view");
				taskMonitor.setProgress(-1);
				
				//repositoryForm.refresh();
				UIManager.getInstance().update();
			}
			
		};
		
		TaskIterator ti = new TaskIterator(task);
		ti.append(utilTask);
		
		CKController.getInstance().getDialogTaskManager().execute(ti);
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
