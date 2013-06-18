package org.cytoscape.cytokegg.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.cytoscape.cytokegg.Plugin;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.task.PathwaySearchTask;
import org.cytoscape.cytokegg.util.Item;
import org.cytoscape.cytokegg.icons.IconLoader;
import org.jdesktop.swingx.VerticalLayout;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class DataSetDialog extends JDialog{
	
	private JList dSetList;
	private JButton delDS, search;
	
	public DataSetDialog(JFrame owner){
		super(owner, "DataSet Selection", true);
		
		this.setSize(500,400);
		
		dSetList = new JList();
		dSetList.setCellRenderer(new ItemRenderer());
		dSetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dSetList.clearSelection();
		dSetList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				delDS.setEnabled(true);
				search.setEnabled(true);
			}
		});
		
		try {
			initList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JButton addNet = new JButton("Import from Network", IconLoader.getInstance().getDatabaseAddIcon());
		addNet.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUIManager.getInstance().setVisibleDialog(Dialogs.NETWORKIMPORT);
			}
		});
		addNet.setSize(new Dimension(100,10));
		
		JButton addFile = new JButton("Import from File", IconLoader.getInstance().getDatabaseAddIcon());
		addFile.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUIManager.getInstance().setVisibleDialog(Dialogs.FILEIMPORT);
			}
		});
		
		delDS = new JButton("Delete dataset", IconLoader.getInstance().getDatabaseDeleteIcon());
		delDS.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Item it = (Item) dSetList.getSelectedValue();
					
					if(!it.equals(null)){
						Repository.getInstance().deleteDataset(it.getId());
						initList();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		delDS.setEnabled(false);
		
		search = new JButton("Search", IconLoader.getInstance().getMagnifierIcon());
		search.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Item it = (Item) dSetList.getSelectedValue();
				try {
					Plugin.getInstance().loadDataSet(it.getId());
					
					PathwaySearchTask task = new PathwaySearchTask();
                    JTaskConfig config = new JTaskConfig();
                    config.displayCancelButton(true);

                    boolean success = TaskManager.executeTask(task, config);
                    if(success){
                    	Plugin.getInstance().setResultPathways(task.getPathways());
                    	GUIManager.getInstance().setVisibleDialog(Dialogs.PATHWAYSELECTION);
                    }
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		search.setEnabled(false);
		
		JPanel side = new JPanel(new VerticalLayout());
		side.add(addFile);
		side.add(addNet);
		side.add(delDS);
		
		JPanel bottom = new JPanel();
		bottom.add(search);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder("Select DataSet"));
		
		panel.add(dSetList, BorderLayout.CENTER);
		panel.add(side, BorderLayout.AFTER_LINE_ENDS);
		panel.add(bottom, BorderLayout.SOUTH);
		
		setContentPane(panel);
	}
	
	private void initList() throws CorruptIndexException, ParseException, IOException{
		
		Item[] dataSets = Repository.getInstance().getIndexedDataSets();
		
		if(dataSets.length == 0){
			delDS.setEnabled(false); 
			search.setEnabled(false);
		}
		
		DefaultListModel model = new DefaultListModel();
		for(int i=0; i<dataSets.length; i++){
			model.addElement(dataSets[i]); 
		}
		dSetList.setModel(model);
	}

}
