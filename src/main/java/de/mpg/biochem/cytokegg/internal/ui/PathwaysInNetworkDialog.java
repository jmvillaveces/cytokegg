package de.mpg.biochem.cytokegg.internal.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.model.events.NetworkDestroyedListener;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskObserver;

import de.mpg.biochem.cytokegg.internal.CyActivator;
import de.mpg.biochem.cytokegg.internal.Pathway;
import de.mpg.biochem.cytokegg.internal.service.KeggService;
import de.mpg.biochem.cytokegg.internal.task.LoopOverPathwaysTask;
import de.mpg.biochem.cytokegg.internal.task.MapIdsTask;
import de.mpg.biochem.cytokegg.internal.util.Item;

public class PathwaysInNetworkDialog extends JDialog implements NetworkAddedListener, NetworkDestroyedListener, TaskObserver{

	
	private List<Item> orgs;
	private JComboBox<Item> organismCB;
	
	private List<String> idType;
	private JComboBox<String> idTypeCB;
	
	private JComboBox<Item> networkCB;
	private JComboBox<String> fieldCB;

	private JTable table;
	
	private JButton search;
	private JButton open;
	private JButton cancel;
	private JScrollPane tablePane;
	private PathwayTableModel tModel;
	
	public PathwaysInNetworkDialog() throws IOException{
		
		//Organisms
		orgs = KeggService.getInstance().getOrganisms();
		
		//Id Types
		idType = Arrays.asList(new String[]{ "ncbi-proteinid", "ncbi-geneid", "uniprot" });
		
		initComponents();
	}
	
	private void initComponents(){
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Search Pathways");
		
		JPanel buttonPanel = new JPanel();
		JPanel organismPanel = new JPanel();
		JLabel organismLabel = new JLabel();
		
		organismLabel.setFont(new java.awt.Font("SansSerif", 0, 12));
		organismLabel.setText("Organism:");
		
		//Organisms ComboCox
		ComboBoxModel<Item> model = new DefaultComboBoxModel<Item>(orgs.toArray(new Item[0]));
		organismCB = new JComboBox<Item>(model);
		organismCB.setRenderer(new ItemRenderer());
		
		// Organisms panel
		
		GroupLayout orgsPanelLayout = new GroupLayout(organismPanel);
		organismPanel.setLayout(orgsPanelLayout);
		orgsPanelLayout.setHorizontalGroup(orgsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(orgsPanelLayout.createSequentialGroup().addContainerGap().addComponent(organismLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(organismCB, 0, 301, Short.MAX_VALUE)));
		
		
		orgsPanelLayout.setVerticalGroup(orgsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						orgsPanelLayout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										orgsPanelLayout
												.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(organismLabel)
												.addComponent(organismCB, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		
		// Network Panel
		JPanel netPanel = new JPanel();
		JLabel netLabel = new JLabel();
		
		netLabel.setFont(new java.awt.Font("SansSerif", 0, 12));
		netLabel.setText("Network:");
		
		networkCB = new JComboBox<Item>();
		networkCB.setEnabled(false);
		networkCB.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				PathwaysInNetworkDialog.this.updateFieldCb();
			}
		});
		
		GroupLayout netPanelLayout = new GroupLayout(netPanel);
		netPanel.setLayout(netPanelLayout);
		netPanelLayout.setHorizontalGroup(netPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(netPanelLayout.createSequentialGroup().addContainerGap().addComponent(netLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(networkCB, 0, 301, Short.MAX_VALUE)));
		
		
		netPanelLayout.setVerticalGroup(orgsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						netPanelLayout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										netPanelLayout
												.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(netLabel)
												.addComponent(networkCB, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		// Field panel
		fieldCB = new JComboBox<String>();
		fieldCB.setEnabled(false);
		
		idTypeCB = new JComboBox<String>(new DefaultComboBoxModel<String>(idType.toArray(new String[0])));
		
		JLabel fieldLabel = new JLabel();
		fieldLabel.setFont(new java.awt.Font("SansSerif", 0, 12));
		fieldLabel.setText("Field:");
		
		JLabel typeLabel = new JLabel();
		typeLabel.setFont(new java.awt.Font("SansSerif", 0, 12));
		typeLabel.setText("Type:");
		
		JPanel fieldPanel = new JPanel();
		GroupLayout fieldLayout = new GroupLayout(fieldPanel);
		fieldPanel.setLayout(fieldLayout);
		fieldLayout.setHorizontalGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(fieldLayout.createSequentialGroup().addContainerGap()
						.addComponent(fieldLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(fieldCB, 0, 301, Short.MAX_VALUE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(typeLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(idTypeCB)
						));
		
		
		fieldLayout.setVerticalGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						fieldLayout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										fieldLayout
												.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(fieldLabel)
												.addComponent(fieldCB, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(typeLabel)
												.addComponent(idTypeCB, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		// Table Panel
		
		table = new JTable();
		
		tModel = new PathwayTableModel();
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tModel);
		table.setModel(tModel);
		table.setRowSorter(sorter);
		
		tablePane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		tablePanel.add(tablePane, BorderLayout.CENTER);
		
		tablePanel.setPreferredSize(new Dimension(600, 300));
				
		// Button Panel
		
		search = new JButton("Search");
		search.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				PathwaysInNetworkDialog.this.search();
			}
			
		});
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();	
			}
		});
		
		GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						buttonPanelLayout.createSequentialGroup().addContainerGap()
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 225, Short.MAX_VALUE)
								.addComponent(cancel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(search).addContainerGap()));
		
		buttonPanelLayout.setVerticalGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						buttonPanelLayout
								.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(
										buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(search).addComponent(cancel))));
		
		//Main Panel
		
		JPanel north = new JPanel();
		north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
		north.add(organismPanel);
		north.add(netPanel);
		north.add(fieldPanel);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.add(north, BorderLayout.NORTH);
		mainPanel.add(tablePanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		
		// Update CBs
		updateNetworkCB();
		updateFieldCb();
		
		setContentPane(mainPanel);
        setModal(true);
        
        pack();
        setVisible(true);
	}
	
	private void updateFieldCb() {
		Item item = (Item) networkCB.getSelectedItem();
		List<String> fields = new ArrayList<String>();
		
		if(item != null){
			
			CyNetwork network = CyActivator.getNetworkManager().getNetwork(Long.parseLong(item.getId()));
			Collection<CyColumn> cols = network.getDefaultNodeTable().getColumns();
			Iterator<CyColumn> i = cols.iterator();
			
			
			while(i.hasNext()){
				CyColumn col = i.next();
				if(col.getType() == String.class)
					fields.add(col.getName());
			}
			
			String[] fieldArr = fields.toArray(new String[0]);
			Arrays.sort(fieldArr);
			
			fieldCB.setModel(new DefaultComboBoxModel<String>(fieldArr));
		}
		
		if(fields.size() > 0){
			fieldCB.setEnabled(true);
			search.setEnabled(true);
		}else{
			fieldCB.setEnabled(false);
			search.setEnabled(false);
		}
	}

	private void updateNetworkCB(){
		CyNetwork[] nets = CyActivator.getNetworkManager().getNetworkSet().toArray(new CyNetwork[0]);
		List<Item> netStr = new ArrayList<Item>();
		
		for(CyNetwork net : nets ){
			netStr.add(new Item(net.getSUID() + "", net.getDefaultNetworkTable().getTitle()));
		}
		
		networkCB.setModel( new DefaultComboBoxModel<Item>(netStr.toArray(new Item[0])));
		networkCB.setRenderer(new ItemRenderer());
		
		if(nets.length > 0){
			networkCB.setEnabled(true);
		}else{
			networkCB.setEnabled(false);
		}
	}
	
	private void search(){
		
		// All stuff needed to begin with the task
		String organism = ((Item) organismCB.getSelectedItem()).getId();
		String target = (String) idTypeCB.getSelectedItem();
		String sourceColumn = (String) fieldCB.getSelectedItem();
		String targetColumn = "KEGG_" + organism;
		CyNetwork network = CyActivator.getNetworkManager().getNetwork(Long.parseLong(((Item) networkCB.getSelectedItem()).getId()));
		CyTable nodesTable = network.getDefaultNodeTable();
		
		// Tasks
		// 1. First map ids to kegg
		
		Task mapTask = new MapIdsTask(target, organism, sourceColumn, targetColumn, nodesTable);
		
		// 2. Loop over organism paths 
		Task loopOverPathwaysTask = new LoopOverPathwaysTask(organism, targetColumn, nodesTable);
		
		TaskIterator taskIterator = new TaskIterator();
		taskIterator.append(mapTask);
		taskIterator.append(loopOverPathwaysTask);
		
		CyActivator.getTaskManager().execute(taskIterator, this);
	}
	
	private void updateTable(Set<Pathway> pathways) {
		
		TableColumn descriptionColumn = table.getColumnModel().getColumn(2);
		descriptionColumn.setCellRenderer(new PathwayCellRenderer());
		
		tModel.setData(pathways);
	}
	
	@Override
	public void handleEvent(NetworkDestroyedEvent e) {
		updateNetworkCB();
	}

	@Override
	public void handleEvent(NetworkAddedEvent e) {
		updateNetworkCB();
	}

	@Override
	public void allFinished(FinishStatus finishStatus) {
	}

	@Override
	public void taskFinished(ObservableTask observableTask) {
		
		if(observableTask.getClass() == LoopOverPathwaysTask.class){
		
			Set<Pathway> pathways = observableTask.getResults(Set.class);
			updateTable(pathways);
		}
	}

}
