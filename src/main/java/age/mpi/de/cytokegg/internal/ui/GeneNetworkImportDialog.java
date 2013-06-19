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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.apache.lucene.index.CorruptIndexException;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.work.TaskIterator;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.task.DataSetNetworkIndexingTask;
import age.mpi.de.cytokegg.internal.util.IconLoader;
import age.mpi.de.cytokegg.internal.util.Item;

public class GeneNetworkImportDialog extends JDialog implements NetworkAddedListener, NetworkAboutToBeDestroyedListener {
	
	private JComboBox networkList;
	private JComboBox nameList;
	private JPanel centerPanel;
	private List<JCheckBox> checkAttr;
	private JButton search;

	public GeneNetworkImportDialog(JDialog owner){
		super(owner, "Network Import", true);
		
		this.setSize(new Dimension(400, 400));
		
		JPanel panel = new JPanel(new BorderLayout());
		
		//North Panel
    	{		
    		/*JButton refresh = new JButton(IconLoader.getInstance().getRefreshIcon());
    		refresh.setToolTipText("Refresh");
    		refresh.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					init();
				}
    		});*/
    		
    		//Network List
    		networkList = new JComboBox();
    		networkList.setRenderer(new ItemRenderer());
    		networkList.setEnabled(false);
    		
    		//Name List
    		nameList = new JComboBox();
    		nameList.setEnabled(false);
    		nameList.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					refreshCheck();
				}
    		});
    		
    		JPanel north = new JPanel(new GridLayout(2, 2));
    		north.setBorder(new TitledBorder("Network"));
    		
    		north.add(new JLabel("Network :"));
    		north.add(networkList);
    		
    		/*JPanel aux = new JPanel();
    		aux.add(refresh);
    		north.add(aux);*/
    		
    		north.add(new JLabel("Name Attribute :"));
    		north.add(nameList);
    		
    		panel.add(north, BorderLayout.NORTH);

    		search = new JButton("search");
    		search.setEnabled(false);
    		search.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					GeneNetworkImportDialog.this.setVisible(false);
					
					Item selected = (Item) networkList.getSelectedItem();
					
					CyNetwork network = CKController.getInstance().getNetMgr().getNetwork(Long.parseLong(selected.getId()));
					String dsName = selected.getDescription();
					
					try {
            			if(Repository.getInstance().isDataSetIndexed(dsName)){
            				int answer = JOptionPane.showConfirmDialog(null, "It seems like the dataset "+dsName+" already exists. Do you want to index it again?");
            				if(answer == JOptionPane.YES_OPTION){
            					Repository.getInstance().deleteDataset(dsName);
            				}else{
            					return;
            				}
            			}
            		} catch (CorruptIndexException e1) {
            			e1.printStackTrace();
            		} catch (IOException e1) {
            			e1.printStackTrace();
            		}
					
					
					List<String> conditions = new ArrayList<String>();
					for(JCheckBox cBox : checkAttr){
						if(cBox.isSelected()){
							conditions.add(cBox.getLabel());
						}
					}
					
					DataSetNetworkIndexingTask task = new DataSetNetworkIndexingTask(dsName, nameList.getSelectedItem().toString() ,conditions, network);
					CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(task));
				}
    		});
    		
    		JPanel aux2 = new JPanel();
    		aux2.add(search);
    		north.add(aux2);
    		
    	}
    	
    	//Center Panel
    	centerPanel = new JPanel(new GridLayout(0, 1));
    	centerPanel.setBorder(new TitledBorder("Expression Attributes"));
    	panel.add(centerPanel, BorderLayout.CENTER);
    	
    	setContentPane(new JScrollPane(panel));
    	
    	init();
	}
	
	public void init(){
		centerPanel.removeAll();
		
		Set<CyNetwork> nets = CKController.getInstance().getNetMgr().getNetworkSet();
		
		Item[] items = new Item[nets.size()];
		int i = 0;
		for(CyNetwork net : nets){
			items[i] = new Item(net.getSUID().toString(), net.getRow(net).get(CyNetwork.NAME, String.class));
			i++;
		}
		ComboBoxModel model = new DefaultComboBoxModel(items);
		networkList.setModel(model);
		
		boolean enabled = (items.length>0) ? true : false;
		networkList.setEnabled(enabled);
		
		if(enabled){
			
			Item item = (Item) networkList.getSelectedItem();
			String[] nodeAttrNames = CyTableUtil.getColumnNames(CKController.getInstance().getNetMgr().getNetwork(Long.parseLong(item.getId())).getDefaultNodeTable()).toArray(new String[0]);
			ComboBoxModel namesModel = new DefaultComboBoxModel(nodeAttrNames);
			nameList.setModel(namesModel);
			nameList.setEnabled(enabled);
			
			search.setEnabled(enabled);
			
			refreshCheck();
		}
	}
	
	private void refreshCheck(){
		centerPanel.removeAll();
		
		checkAttr = new ArrayList<JCheckBox>();
		Item item = (Item) networkList.getSelectedItem();
		String[] nodeAttrNames = CyTableUtil.getColumnNames(CKController.getInstance().getNetMgr().getNetwork(Long.parseLong(item.getId())).getDefaultNodeTable()).toArray(new String[0]);
		for(String name : nodeAttrNames){
			if(!name.equals(nameList.getSelectedItem().toString())){
				JCheckBox chk = new JCheckBox(name);
				centerPanel.add(chk);
				checkAttr.add(chk);
			}
		}
		centerPanel.updateUI();
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		CyNetwork net = e.getNetwork();
		
		DefaultComboBoxModel model = (DefaultComboBoxModel) networkList.getModel();
		for(int i=0; i<model.getSize(); i++){
			Item item = (Item) model.getElementAt(i);
			if(item.getId().equals(net.getSUID().toString())){
				model.removeElementAt(i);
				break;
			}
		}
		refreshCheck();
	}

	@Override
	public void handleEvent(NetworkAddedEvent e) {
		CyNetwork net = e.getNetwork();
		((DefaultComboBoxModel) networkList.getModel()).addElement(new Item(net.getSUID().toString(), net.getRow(net).get(CyNetwork.NAME, String.class)));
		refreshCheck();
	}
}
