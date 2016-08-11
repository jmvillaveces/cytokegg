package de.mpg.biochem.cytokegg.internal.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.model.events.NetworkDestroyedListener;
import org.cytoscape.work.TaskIterator;

import de.mpg.biochem.cytokegg.internal.CyActivator;
import de.mpg.biochem.cytokegg.internal.service.KeggService;
import de.mpg.biochem.cytokegg.internal.task.NetworkCreationTask;
import de.mpg.biochem.cytokegg.internal.util.Item;

public class PathwaysInNetworkDialog extends JDialog implements NetworkAddedListener, NetworkDestroyedListener{

	
	private List<Item> orgs;
	private JComboBox<Item> orgsCB;
	
	private List<String> idType;
	private JComboBox<String> idTypeCB;
	
	private JComboBox<Item> networkCB;
	private JComboBox<Item> fieldCB;

	
	private JButton search;
	private JButton open;
	
	public PathwaysInNetworkDialog() throws IOException{
		
		//Organisms
		orgs = KeggService.getInstance().getOrganisms();
		
		//Id Types
		idType = Arrays.asList(new String[]{"genes", "ncbi-proteinid", "ncbi-geneid", "uniprot"});
		
		initComponents();
	}
	
	private void initComponents(){
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Search Pathways");
		
		JPanel panel = new JPanel(new BorderLayout());
		
		search = new JButton("Search");
		search.setEnabled(false);
		search.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
			
		});
		
		open = new JButton("Open");
		open.setEnabled(false);
		
		open.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		//Organisms ComboCox
		ComboBoxModel model = new DefaultComboBoxModel(orgs.toArray());
		orgsCB = new JComboBox(model);
		orgsCB.setRenderer(new ItemRenderer());
		
		fieldCB = new JComboBox();
		fieldCB.setEnabled(false);
		
		networkCB = new JComboBox();
		networkCB.setEnabled(false);
		updateNetworkCB();
		
		networkCB.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent itemEvent) {
				Item item = (Item) networkCB.getSelectedItem();
				
				CyNetwork network = CyActivator.getNetworkManager().getNetwork(Long.parseLong(item.getId()));
				Collection<CyColumn> cols = network.getDefaultNodeTable().getColumns();
				Iterator<CyColumn> i = cols.iterator();
				
				List<String> fields = new ArrayList<String>();
				while(i.hasNext()){
					CyColumn col = i.next();
					fields.add(col.getName());
				}
				
				fieldCB.setModel( new DefaultComboBoxModel(fields.toArray()));
				
				if(fields.size() > 0){
					fieldCB.setEnabled(true);
				}else{
					fieldCB.setEnabled(false);
				}
				
			}
		});
		
	
		idTypeCB = new JComboBox(new DefaultComboBoxModel(idType.toArray()));
		
		{
			JPanel aux = new JPanel();
			aux.add(orgsCB);
			aux.add(networkCB);
			aux.add(fieldCB);
			aux.add(idTypeCB);
			
			panel.add(aux, BorderLayout.CENTER);
		}
		
		
		setContentPane(panel);
        setModal(true);
        
        pack();
        setVisible(true);
	}
	
	private void updateNetworkCB(){
		CyNetwork[] nets = CyActivator.getNetworkManager().getNetworkSet().toArray(new CyNetwork[0]);
		List<Item> netStr = new ArrayList<Item>();
		
		for(CyNetwork net : nets ){
			netStr.add(new Item(net.getSUID() + "", net.getDefaultNetworkTable().getTitle()));
		}
		
		networkCB.setModel( new DefaultComboBoxModel(netStr.toArray()));
		networkCB.setRenderer(new ItemRenderer());
		
		if(nets.length > 0){
			networkCB.setEnabled(true);
		}else{
			networkCB.setEnabled(false);
		}
	}
	
	@Override
	public void handleEvent(NetworkDestroyedEvent e) {
		updateNetworkCB();
	}

	@Override
	public void handleEvent(NetworkAddedEvent e) {
		updateNetworkCB();
	}

}
