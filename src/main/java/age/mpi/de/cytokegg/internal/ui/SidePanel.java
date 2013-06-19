package age.mpi.de.cytokegg.internal.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.jdesktop.swingx.VerticalLayout;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.ui.widget.RangeSlider;
import age.mpi.de.cytokegg.internal.util.IconLoader;
import age.mpi.de.cytokegg.internal.util.Item;
import age.mpi.de.cytokegg.internal.util.PluginProperties;


public class SidePanel extends JPanel implements CytoPanelComponent, NetworkAddedListener, NetworkAboutToBeDestroyedListener {

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
		
		Set<CyNetwork> nets = CKController.getInstance().getNetMgr().getNetworkSet();
		Item[] items = new Item[nets.size()+1];
		
		items[0] = new Item("-1", "-Select a Network-");
		int i = 1;
		for(CyNetwork net : nets){
			items[i] = new Item(net.getSUID().toString(), net.getRow(net).get(CyNetwork.NAME, String.class));
			i++;
		}
		ComboBoxModel model = new DefaultComboBoxModel(items);
		
		//Pathways combo box
		pBox = new JComboBox(model);
		pBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(cBox.getSelectedIndex() != 0){
					pathwayChange();
				}else{
					((DefaultComboBoxModel) gBox.getModel()).removeAllElements();
					((DefaultComboBoxModel) cBox.getModel()).removeAllElements();
				}
			}
		});
		
		JPanel pathPanel = new JPanel();
		pathPanel.setBorder(new TitledBorder("Select a Network"));
		pathPanel.add(pBox);
		
		add(pathPanel);
		
		//GEA Panel
		gBox = new JComboBox();
		
		JButton butt = new JButton("find",IconLoader.getInstance().getMagnifierIcon());
		butt.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				UIManager.getInstance().openExpressionWindow(gBox.getSelectedItem().toString());
			}
		});
		
		GEAExpression = new JPanel();
		GEAExpression.setBorder(new TitledBorder("Expression Profile"));
		
		GEAExpression.add(gBox);
		GEAExpression.add(butt);
		
		GEAExpression.setEnabled(false);
		add(GEAExpression);
		
		initConditionsPanel();
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
					if(!cBox.getSelectedItem().toString().equals(defValue)){
						int condition = cBox.getSelectedIndex();
						Item pathway = (Item) pBox.getSelectedItem();
						setCondition(pathway, condition);
					}
				}
	        });
	        setButton.setEnabled(false);
	        
	        timeField = new JTextField(2+"");
	        
	        JLabel timeLabel = new JLabel();
	        timeLabel.setIcon(IconLoader.getInstance().getTimeIcon());
	        
	        JPanel aux = new JPanel();
	        aux.add(setButton);
	        aux.add(playButton);
	        aux.add(stopButton);
	        aux.add(timeField);
	        aux.add(timeLabel);
	        
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
	
	private void pathwayChange(){
		Item selected = (Item) pBox.getSelectedItem();
		CyNetwork pathwayNet = CKController.getInstance().getNetMgr().getNetwork(Long.parseLong(selected.getId()));
		CyRootNetwork rootNet = ((CySubNetwork) pathwayNet).getRootNetwork();
		
		CyTable nodeTable = rootNet.getDefaultNodeTable();
		
		Collection<CyRow> genes = nodeTable.getMatchingRows("KEGG.entry", "gene");
		Collection<CyRow> orthologs = nodeTable.getMatchingRows("KEGG.entry", "ortholog");
		
		List<String> gen = new ArrayList<String>();
		
		Iterator<CyRow> i = genes.iterator();
		while(i.hasNext()){
			CyRow row = i.next();
			
			String[] names = row.get("KEGG.name", String.class).split(" ");
			
			if(!gen.contains(names[0]))
				gen.add(names[0]);
		}
		
		i = orthologs.iterator();
		while(i.hasNext()){
			CyRow row = i.next();
			
			String[] names = row.get("KEGG.name", String.class).split(" ");
			
			if(!gen.contains(names[0]))
				gen.add(names[0]);
		}
		
		String[] genArr = gen.toArray(new String[0]);
		Arrays.sort(genArr);
		
		ComboBoxModel gBoxModel = new DefaultComboBoxModel(genArr);
		gBox.setModel(gBoxModel);
		
		CyTable netTable = rootNet.getDefaultNetworkTable();
		CyRow netRow = netTable.getRow(rootNet.getSUID());
		
		List<String> conditions = netRow.getList("conditions", String.class);
		//conditions.add(0, defValue);
		
		List<String> nConditions = new ArrayList<String>();
		nConditions.addAll(conditions);
		nConditions.add(0, defValue);
		
		DefaultComboBoxModel cBoxModel = new DefaultComboBoxModel(nConditions.toArray(new String[0]));
		cBox.setModel(cBoxModel);
		
		//RangerSlider
		double min = netRow.get("min", Double.class)*10;
		double max = netRow.get("max", Double.class)*10;
		
		slider.setMinimum((int) min);
        slider.setMaximum((int) max);
        
        double value = (max+min)/2;
        slider.setValue((int) value);
        slider.setUpperValue((int) value);
		
	}
	
	private void setCondition(Item pathway, int condition){
		
		double min = Double.parseDouble(slider.getValue()+"")/10;
    	double max = Double.parseDouble(slider.getUpperValue()+"")/10;
    	
    	CyNetwork selectedNet = CKController.getInstance().getNetMgr().getNetwork(Long.parseLong(pathway.getId()));
    	CyRootNetwork rootNet = ((CySubNetwork) CKController.getInstance().getNetMgr().getNetwork(Long.parseLong(pathway.getId()))).getRootNetwork();
    	
    	Collection<CyRow> rows = rootNet.getDefaultNodeTable().getMatchingRows("hasExpression", true);
    	Collection<CyNetworkView> netViews = CKController.getInstance().getNetworkViewManager().getNetworkViews(selectedNet);
    	CyNetworkView netView = netViews.iterator().next();
    	
    	Iterator<CyRow> i = rows.iterator();
    	while(i.hasNext()){
    		CyRow row = i.next();
    		CyNode node = rootNet.getNode(row.get("SUID", Long.class));
    		
    		if(condition == 0){
    			netView.getNodeView(node).clearValueLock(BasicVisualLexicon.NODE_FILL_COLOR);
    		}else{
    			condition --;
    			Double exValue = row.getList("expression", Double.class).get(condition);
    			
	    		if(exValue>max){
	    			netView.getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.RED);
	    		}else if(exValue<min){
	    			netView.getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.BLUE);
	    		}else{
	    			netView.getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.GREEN);
	    		}
    		}
    		netView.updateView();
    	}
	}
	
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public String getTitle() {
		return PluginProperties.getInstance().getPluginName();
	}
	
	@Override
	public Icon getIcon() {
		return null;
	}
	
	@Override
	public void handleEvent(NetworkAddedEvent e) {
		CyNetwork net = e.getNetwork();
		((DefaultComboBoxModel) pBox.getModel()).addElement(new Item(net.getSUID().toString(), net.getRow(net).get(CyNetwork.NAME, String.class)));
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		CyNetwork net = e.getNetwork();
		
		DefaultComboBoxModel model = (DefaultComboBoxModel) pBox.getModel();
		for(int i=0; i<model.getSize(); i++){
			Item item = (Item) model.getElementAt(i);
			if(item.getId().equals(net.getSUID().toString())){
				model.removeElementAt(i);
				break;
			}
		}
		
		((DefaultComboBoxModel) gBox.getModel()).removeAllElements();
		((DefaultComboBoxModel) cBox.getModel()).removeAllElements();
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
	    	
	    	Item pathway = (Item) pBox.getSelectedItem();
	    	
	    	while(run){
	    		for(int i=0; i<cBox.getItemCount(); i++){
	    			
	    			if(!run){
	    				break;
	    			}
					
	    			cBox.setSelectedIndex(i);
	    			setCondition(pathway, i);
					
					try {
						int time = 2000;
						try{
							time = Integer.parseInt(timeField.getText()) * 1000;
						}catch(Exception e){
							e.printStackTrace();
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
