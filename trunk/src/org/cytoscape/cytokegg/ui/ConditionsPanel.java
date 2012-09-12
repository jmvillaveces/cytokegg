package org.cytoscape.cytokegg.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.cytokegg.DataSet;
import org.cytoscape.cytokegg.Plugin;
import org.cytoscape.cytokegg.ui.widget.RangeSlider;
import org.cytoscape.cytokegg.icons.IconLoader;

public class ConditionsPanel extends JPanel{
	
	private JComboBox cBox;
	private String defValue = "-Select a condition-";
	private RangeSlider slider;
	private JLabel minLabel, maxLabel;
	private JTextField timeField;
	private JButton setButton, playButton, stopButton;
	private PlayThread thread = null;
	private boolean playRunning = false;
	
	public ConditionsPanel(){
		
		final DataSet dataSet = Plugin.getInstance().getCurrentDataSet();
		
	}
	
	class PlayThread extends Thread {
	    private boolean run = true;
		
	    public PlayThread(){
	    	super();
	    }
	    
		// This method is called when the thread runs
	    public void run() {
	    	cBox.setEnabled(false);
	    	setButton.setEnabled(false);
	    	stopButton.setEnabled(true);
	    	
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
					//Plugin.getInstance().getMapper().calculateExpression(Plugin.getInstance().getNetwork(), condition, min, max);
					
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
