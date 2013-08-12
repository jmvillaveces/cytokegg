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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.cytoscape.work.TaskIterator;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.task.NetworkCreationTask;
import age.mpi.de.cytokegg.internal.ui.widget.AutoHighlightTextField;
import age.mpi.de.cytokegg.internal.util.Item;

public class BrowsePathwayDialog extends JDialog{
	
	private Item[] pathways, orgs;
	private JList list;
	private JComboBox cBox;
	private DefaultComboBoxModel cBoxModel;
	private JButton select;
	private AutoHighlightTextField searchField;
	
	public BrowsePathwayDialog(JFrame owner){
    	super(owner, "File Import", true);
    	this.setSize(new Dimension(400, 400));
    	
		try {
			orgs = Repository.getInstance().getIndexedOrganisms();
		} catch (Exception e) {
			orgs = new Item[0];
		}
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder("Browse Pathways"));
		
		//select button
		select = new JButton("Select");
		select.setEnabled(false);
		select.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Item i = (Item)list.getSelectedValue();
				String pathId = i.getId().substring(i.getId().lastIndexOf(":")+1,i.getId().length());
				
				try {
					TaskIterator ti = new TaskIterator(new NetworkCreationTask(new URL("http://www.kegg.jp/kegg-bin/download?entry="+pathId+"&format=kgml"), false));
					CKController.getInstance().getDialogTaskManager().execute(ti);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		
		//list
		list = new JList();
		list.setCellRenderer(new ItemRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.clearSelection();
		list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				select.setEnabled(true);
			}
		});
		
		//Combo model
		cBoxModel = new DefaultComboBoxModel(orgs);
		
		//Combo box
		cBox = new JComboBox(cBoxModel);
		cBox.setRenderer(new ItemRenderer());
		cBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					updateList();
				} catch (ParseException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		//Search Field
        searchField = new AutoHighlightTextField(20);
        searchField.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					updateList();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
        	
        });
        
        {
        	JPanel aux = new JPanel();
        	aux.add(cBox);
        	aux.add(searchField);
        	panel.add(aux, BorderLayout.NORTH);
        }
        
        {
        	JPanel aux = new JPanel();
        	aux.add(select);
        	panel.add(aux, BorderLayout.SOUTH);
        }
        
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        setContentPane(panel);
	}
	
	public void setVisible(boolean bool){
		if(bool){
			try {
				orgs = Repository.getInstance().getIndexedOrganisms();
				
				//Combo model
				cBoxModel = new DefaultComboBoxModel(orgs);
				cBox.setModel(cBoxModel);
				updateList();
				
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.setVisible(bool);
	}
	
	private void updateList() throws ParseException, IOException{
		String query = searchField.getText();
		Item item = (Item) cBox.getSelectedItem();
		
		if(item != null){
		
			if(query.length()<3)
				this.pathways = Repository.getInstance().getPathwaysByOrganism(item.getId());
			else
				this.pathways = Repository.getInstance().getPathwaysByOrganismAndText(item.getId(), "*"+query+"*");
			
			DefaultListModel model = new DefaultListModel();
			for(int i=0; i<pathways.length; i++){
				model.addElement(pathways[i]); 
			}
			list.setModel(model);
		}
		list.clearSelection();
		select.setEnabled(false);
	}
}
