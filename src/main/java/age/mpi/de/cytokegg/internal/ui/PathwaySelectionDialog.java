/**
 * Copyright 2013 JosŽ Mar’a Villaveces Max Planck institute for biology of
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
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cytoscape.work.TaskIterator;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.task.NetworkCreationTask;
import age.mpi.de.cytokegg.internal.util.PathwayItem;

public class PathwaySelectionDialog extends JDialog implements Updatable{
	
	private JButton select;
	private JList pathwayList;
	
	public PathwaySelectionDialog(JFrame reference) throws Exception{
		super(reference, "Pathway Selection", false);
		
		this.setSize(500,400);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder("Select a pathway"));
		
		//Select button
		select = new JButton("Select");
		select.setEnabled(false);
		select.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				PathwayItem i = (PathwayItem)pathwayList.getSelectedValue();
				String pathId = i.getId().substring(i.getId().lastIndexOf(":")+1,i.getId().length());
				
				try {
					PathwaySelectionDialog.this.setVisible(false);
					TaskIterator ti = new TaskIterator(new NetworkCreationTask(new URL("http://www.kegg.jp/kegg-bin/download?entry="+pathId+"&format=kgml"), true));
					CKController.getInstance().getDialogTaskManager().execute(ti);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				/*Task task = new PaintPathwayTask(pathId);
		        JTaskConfig config = new JTaskConfig();
		        config.displayCancelButton(false);
		        config.displayStatus(true);
		        config.displayTimeElapsed(true);
		        
		        boolean success = TaskManager.executeTask(task, config);
		        if(success){
		        	setVisible(false);
		        	GUIManager.getInstance().setSidePanel();
		        }*/
			}
		});
		
		JPanel aux = new JPanel();
		aux.add(select);
		panel.add(aux, BorderLayout.SOUTH);
		
		//pathways list
		pathwayList = new JList();
		pathwayList.setCellRenderer(new ItemRenderer());
		pathwayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pathwayList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				boolean flag = (pathwayList.getSelectedIndex() > -1) ? true : false;
				select.setEnabled(flag);
			}
		});
		
		panel.add(new JScrollPane(pathwayList), BorderLayout.CENTER);
		setContentPane(panel);
	}
	
	public void update() throws Exception {
		
		PathwayItem[] pathwayItems = CKController.getInstance().getResultPathways();
		
		DefaultListModel model = new DefaultListModel();
		model.clear();
		for(PathwayItem i : pathwayItems){
			model.addElement(i);
		}
		pathwayList.setModel(model);
		pathwayList.clearSelection();
		pathwayList.updateUI();
	}

}
