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

import org.cytoscape.cytokegg.Plugin;
import org.cytoscape.cytokegg.task.PaintPathwayTask;
import org.cytoscape.cytokegg.util.Item;
import org.cytoscape.cytokegg.util.PathwayItem;

import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class PathwaySelectionDialog extends JDialog {
	
	private JButton select;
	private JList pathwayList;
	
	public PathwaySelectionDialog(JFrame owner){
		super(owner, "Pathway Selection", true);
		
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
				
				Task task = new PaintPathwayTask(pathId);
		        JTaskConfig config = new JTaskConfig();
		        config.displayCancelButton(false);
		        config.displayStatus(true);
		        config.displayTimeElapsed(true);
		        
		        boolean success = TaskManager.executeTask(task, config);
		        if(success){
		        	setVisible(false);
		        	GUIManager.getInstance().setSidePanel();
		        }
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
		setListValues(Plugin.getInstance().getResultPathways());
		
		panel.add(new JScrollPane(pathwayList), BorderLayout.CENTER);
		setContentPane(panel);
	}
	
	private void setListValues(PathwayItem[] pathwayItems){
		DefaultListModel model = new DefaultListModel();
		model.clear();
		for(PathwayItem i : pathwayItems){
			model.addElement(i);
		}
		pathwayList.setModel(model);
		pathwayList.clearSelection();
	}

}
