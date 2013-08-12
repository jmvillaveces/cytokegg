/**
 * Copyright 2013 José María Villaveces Max Planck institute for biology of
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.jdesktop.swingx.VerticalLayout;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.task.PathwaySearchTask;
import age.mpi.de.cytokegg.internal.util.IconLoader;
import age.mpi.de.cytokegg.internal.util.Item;


public class DataSetDialog extends JDialog implements Updatable{
	
	private JList dSetList;
	private JButton delDS, search;
	
	public DataSetDialog(JFrame owner){
		super(owner, "DataSet Selection", false);
		
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
		
		JButton addNet = new JButton("Import from Network", IconLoader.getInstance().getDatabaseAddIcon());
		addNet.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JDialog geneNetworkImportDialog = new GeneNetworkImportDialog(DataSetDialog.this);
				geneNetworkImportDialog.setVisible(true);
			}
		});
		addNet.setSize(new Dimension(100,10));
		
		JButton addFile = new JButton("Import from File", IconLoader.getInstance().getDatabaseAddIcon());
		addFile.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//UIManager.getInstance().setVisibleDialog(DialogEnum.FILEIMPORT);
				JDialog geneListImportDialog = new GeneListImportDialog(DataSetDialog.this);
				geneListImportDialog.setVisible(true);
			}
		});
		
		delDS = new JButton("Delete dataset", IconLoader.getInstance().getDatabaseDeleteIcon());
		delDS.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Task utilTask = new AbstractTask(){

					@Override
					public void run(TaskMonitor arg0) throws Exception {
						Item it = (Item) dSetList.getSelectedValue();
						if(!it.equals(null)){
							Repository.getInstance().deleteDataset(it.getId());
							update();
						}	
					}
					
				};
				CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(utilTask));
			}
		});
		delDS.setEnabled(false);
		
		search = new JButton("Search", IconLoader.getInstance().getMagnifierIcon());
		search.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Item it = (Item) dSetList.getSelectedValue();
				try{
					UIManager.getInstance().setVisibleDialog(DialogEnum.PATHWAYSELECTION);
					CKController.getInstance().loadDataSet(it.getId());
					CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(new PathwaySearchTask()));
				}catch(Exception e){
					UIManager.getInstance().setVisibleDialog(DialogEnum.DATASET);
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
		
		try {
			initList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initList() throws CorruptIndexException, ParseException, IOException{
		
		Item[] dataSets = Repository.getInstance().getIndexedDataSets();
		
		if(dataSets != null){
			if(dataSets.length == 0){
				delDS.setEnabled(false); 
				search.setEnabled(false);
			}
			
			DefaultListModel model = new DefaultListModel();
			for(int i=0; i<dataSets.length; i++){
				System.out.println("from repos ->"+dataSets[i]);
				model.addElement(dataSets[i]);
			}
			dSetList.setModel(model);
		}
	}

	@Override
	public void update() throws Exception {
		initList();
		this.repaint();
		//dSetList.repaint();
		//dSetList.updateUI();
	}

}
