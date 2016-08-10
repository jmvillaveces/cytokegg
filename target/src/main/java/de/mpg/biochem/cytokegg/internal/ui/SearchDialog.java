package de.mpg.biochem.cytokegg.internal.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskObserver;

import de.mpg.biochem.cytokegg.internal.CyActivator;
import de.mpg.biochem.cytokegg.internal.service.KeggService;
import de.mpg.biochem.cytokegg.internal.task.NetworkCreationTask;
import de.mpg.biochem.cytokegg.internal.task.PathwaySearchTask;
import de.mpg.biochem.cytokegg.internal.ui.widget.AutoHighlightTextField;
import de.mpg.biochem.cytokegg.internal.util.Item;

public class SearchDialog extends JDialog implements TaskObserver {

	private List<Item> orgs;
	private JButton search;
	private JButton open;
	private AutoHighlightTextField searchField;
	private JComboBox<Item> orgsCB;
	private JList<Item> list;
	
	public SearchDialog() throws IOException{
		
		super(CyActivator.getCySwingApplication().getJFrame(), ModalityType.APPLICATION_MODAL);
		
		//Organisms
		orgs = KeggService.getInstance().getOrganisms();
		
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
				
				if(searchField.getText().isEmpty()) return;
				
				
				PathwaySearchTask task = new PathwaySearchTask();				
				Item org = (Item) orgsCB.getSelectedItem();
				task.setOrg(org.getId());
				task.setQuery(searchField.getText());
				
				CyActivator.getTaskManager().execute(new TaskIterator(task), SearchDialog.this);			
			}
		});
		
		open = new JButton("Open");
		open.setEnabled(false);
		
		open.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Item pathway = list.getSelectedValue();
				
				try {
					NetworkCreationTask task = new NetworkCreationTask(new URL("http://rest.kegg.jp/get/"+ pathway.getId() +"/kgml"));
					CyActivator.getTaskManager().execute(new TaskIterator(task), SearchDialog.this);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
			}
		});
		
		//Organisms ComboCox
		ComboBoxModel model = new DefaultComboBoxModel(orgs.toArray());
		orgsCB = new JComboBox(model);
		orgsCB.setRenderer(new ItemRenderer());
		
		//Pathway jlist
		list = new JList();
		list.setCellRenderer(new ItemRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.clearSelection();
		
		//Search Field
        searchField = new AutoHighlightTextField(20);
        searchField.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(searchField.getText().length() > 0){
					search.setEnabled(true);
				}else{
					search.setEnabled(false);
				}
			}
        	
        });
		
		{
        	JPanel aux = new JPanel();
        	aux.add(orgsCB);
        	aux.add(searchField);
        	aux.add(search);
        	panel.add(aux, BorderLayout.NORTH);
        }
        
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        
        {
        	JPanel aux = new JPanel();
        	aux.add(open);
        	panel.add(aux, BorderLayout.SOUTH);
        }
        
        setContentPane(panel);
        setModal(true);
        
        pack();
        setVisible(true);
		
	}

	@Override
	public void allFinished(FinishStatus status) {
		
	}

	@Override
	public void taskFinished(ObservableTask task) {
		List<Item> results = task.getResults(List.class);
		
		DefaultListModel model = new DefaultListModel<Item>();
		for(Item path : results){
			model.addElement(path);
		}
		list.setModel(model);
		
		if(results.size() > 0){
			list.setSelectedIndex(0);
			open.setEnabled(true);
		}else{
			open.setEnabled(false);
		}
	}
	
}
