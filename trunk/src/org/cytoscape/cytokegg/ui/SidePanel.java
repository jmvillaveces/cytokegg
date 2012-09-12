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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.lucene.index.CorruptIndexException;
import org.cytoscape.cytokegg.DataSet;
import org.cytoscape.cytokegg.Pathway;
import org.cytoscape.cytokegg.Plugin;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.ui.widget.RangeSlider;
import org.cytoscape.cytokegg.icons.IconLoader;
import org.jdesktop.swingx.VerticalLayout;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class SidePanel extends JPanel {
	
	private JComboBox pBox, gBox, cBox;
	private JPanel GEAExpression, conditionsPanel;
	private String defValue = "-Select a condition-";
	private RangeSlider slider;
	private JLabel minLabel, maxLabel;
	private JTextField timeField;
	private JButton setButton, playButton, stopButton;
	private PlayThread thread = null;
	private boolean playRunning = false;
	
	public SidePanel(){
		
		setLayout(new VerticalLayout());
		
		//Pathways combo box
		pBox = new JComboBox(Plugin.getInstance().getPathways());
		pBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pathwayChange();
			}
		});
		
		JPanel pathPanel = new JPanel();
		pathPanel.setBorder(new TitledBorder("Select a Pathway"));
		pathPanel.add(pBox);
		
		add(pathPanel);
		
		//GEA Panel
		gBox = new JComboBox();
		
		JButton butt = new JButton(IconLoader.getInstance().getMagnifierIcon());
		butt.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUIManager.getInstance().openExpressionWindow(gBox.getSelectedItem().toString());
			}
		});
		
		GEAExpression = new JPanel();
		GEAExpression.setBorder(new TitledBorder("Select a gene"));
		
		GEAExpression.add(gBox);
		GEAExpression.add(butt);
		
		GEAExpression.setEnabled(false);
		add(GEAExpression);
		
		initConditionsPanel();
		
		pathwayChange();
	}

	private void initGeneBox(){
		String pathwayId = Plugin.getInstance().getPathway(pBox.getSelectedItem().toString()).getName();
		
		try {
			String[] genes = Repository.getInstance().getGenesByPathway(pathwayId);
			
			ComboBoxModel gBoxModel = new DefaultComboBoxModel(genes);
			gBox.setModel(gBoxModel);
			
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void pathwayChange(){
		
		Pathway pathway = Plugin.getInstance().getPathway(pBox.getSelectedItem().toString());
		CyNetwork network = pathway.getNetwork();
		
		CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		int[] inArr = network.getNodeIndicesArray();		
		
		DataSet dataSet = Plugin.getInstance().getDataSet(pathway.getDataSet());
		if(dataSet != null){
			String[] cList = dataSet.getConditions();
			Arrays.sort(cList);
			
			String[] conditions = new  String[cList.length+1];
			conditions[0] = defValue;
			
			for(int i=0; i<cList.length; i++){
				conditions[i+1] = cList[i];
			}
			
			//Combo model
			DefaultComboBoxModel cBoxModel = new DefaultComboBoxModel(conditions);
			cBox.setModel(cBoxModel);
			
			//RangerSlider
			double min = dataSet.getMin()*10;
			double max = dataSet.getMax()*10;
			
			slider.setMinimum((int) min);
	        slider.setMaximum((int) max);
	        
	        double value = (max+min)/2;
	        slider.setValue((int) value);
	        slider.setUpperValue((int) value);
	        
	        conditionsPanel.setVisible(true);
		}else{
	        conditionsPanel.setVisible(false);
		}
		
		initGeneBox();
	}
	
	private void initConditionsPanel() {
		
		conditionsPanel = new JPanel();

		//Range Panel
		{	
			//Combo box
			cBox = new JComboBox();
			cBox.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!playRunning){
						if(cBox.getSelectedIndex() == 0)
							setButton.setEnabled(false);
						else
							setButton.setEnabled(true);
					}
				}
			});
			
			slider = new RangeSlider();
	        
	        minLabel = new JLabel(String.valueOf(slider.getValue())); 
	        maxLabel = new JLabel(String.valueOf(slider.getUpperValue()));
			
	        slider.addChangeListener(new ChangeListener() {
	            public void stateChanged(ChangeEvent e) {
	                
	            	double min = Double.parseDouble(slider.getValue()+"")/10;
	            	double max = Double.parseDouble(slider.getUpperValue()+"")/10;
	            	minLabel.setText(""+min);
	                maxLabel.setText(""+max);
	            }
	        });
	        
	        stopButton = new JButton(IconLoader.getInstance().getStopIcon());
	        stopButton.setEnabled(false);
	        stopButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					if(thread != null){
	        			thread.stopExecution();
	        		}
					stopButton.setEnabled(false);
					playButton.setEnabled(true);
			    	cBox.setEnabled(true);
			    	pBox.setEnabled(true);
				}
	        	
	        });
	        
	        playButton = new JButton(IconLoader.getInstance().getPlayIcon());
	        playButton.setEnabled(true);
	        playButton.addActionListener(new ActionListener(){
	        	@Override
				public void actionPerformed(ActionEvent arg0) {
	        		if(thread != null){
	        			thread.stopExecution();
	        		}
	        		playButton.setEnabled(false);
	        		
	        		thread = new PlayThread();
	        		thread.start();
				}
	        });
	        
	        setButton = new JButton("Set");
	        setButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					String condition = "";
					if(!cBox.getSelectedItem().toString().equals(defValue)){
						condition = cBox.getSelectedItem().toString();
					}
					
					double min = Double.parseDouble(slider.getValue()+"")/10;
	            	double max = Double.parseDouble(slider.getUpperValue()+"")/10;
					
	            	Pathway pathway = Plugin.getInstance().getPathway(pBox.getSelectedItem().toString());
	            	pathway.getMapper().calculateExpression(pathway.getNetwork(), condition, min, max);
				}
	        });
	        setButton.setEnabled(false);
	        
	        timeField = new JTextField(2+"");
	        
	        JPanel aux = new JPanel();
	        aux.add(setButton);
	        aux.add(playButton);
	        aux.add(stopButton);
	        aux.add(timeField);
	        
	        JPanel conditionsPanel = new JPanel(new BorderLayout());
	        conditionsPanel.setBorder(new TitledBorder("Gene Expression"));
	        
	        conditionsPanel.add(cBox, BorderLayout.PAGE_START);
	        conditionsPanel.add(minLabel, BorderLayout.LINE_START);
	        conditionsPanel.add(slider, BorderLayout.CENTER);
	        conditionsPanel.add(maxLabel, BorderLayout.LINE_END); 
	        conditionsPanel.add(aux, BorderLayout.PAGE_END);
	        
	        add(conditionsPanel);
		}
	}
	
	class PlayThread extends Thread {
	    private boolean run = true;
		
	    public PlayThread(){
	    	super();
	    }
	    
		// This method is called when the thread runs
	    public void run() {
	    	pBox.setEnabled(false);
	    	cBox.setEnabled(false);
	    	setButton.setEnabled(false);
	    	stopButton.setEnabled(true);
	    	
	    	Pathway pathway = Plugin.getInstance().getPathway(pBox.getSelectedItem().toString());
	    	
	    	while(run){
	    		for(int i=0; i<cBox.getItemCount(); i++){
	    			
	    			if(!run){
	    				break;
	    			}
	    			
	    			String condition = "";
					
	    			cBox.setSelectedIndex(i);
	    			if(!cBox.getSelectedItem().toString().equals(defValue)){
						condition = cBox.getSelectedItem().toString();
					}
					
					double min = Double.parseDouble(slider.getValue()+"")/10;
	            	double max = Double.parseDouble(slider.getUpperValue()+"")/10;
					
	            	pathway.getMapper().calculateExpression(pathway.getNetwork(), condition, min, max);
					
					try {
						int time = 2000;
						try{
							time = Integer.parseInt(timeField.getText()) *1000;
						}catch(Exception e){
							
						}
						sleep(time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    	}
	    	return;
	    }
	    
	    public void stopExecution(){
	    	this.run = false;
	    }
	}
}
