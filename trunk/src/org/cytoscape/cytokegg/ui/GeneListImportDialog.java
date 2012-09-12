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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.lucene.index.CorruptIndexException;
import org.cytoscape.cytokegg.Plugin;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.task.DataSetFileIndexingTask;
import org.cytoscape.cytokegg.task.PathwaySearchTask;
import org.cytoscape.cytokegg.task.ReadFileTask;
import org.cytoscape.cytokegg.util.Item;
import org.cytoscape.cytokegg.util.PluginProperties;
import org.cytoscape.cytokegg.util.TextPlainFileFilter;
import org.cytoscape.cytokegg.icons.IconLoader;

import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class GeneListImportDialog extends JDialog{
	
	private JCheckBox titlesCheckBox;
    private JTable table;
    private JButton openButton, nextButton;
    private Item[] pathways;
    private Vector<Vector> data;
	protected String dsName;
    
    public GeneListImportDialog(JFrame owner){
    	super(owner, "File Import", true);
    	
    	this.setSize(new Dimension(400, 400));
    	
    	data = new Vector(0);
    	
    	JPanel panel = new JPanel(new BorderLayout());
    	panel.setBorder(new TitledBorder("File import"));
    	
    	//Titles CheckBox
    	titlesCheckBox = new JCheckBox("Include Titles", false);
    	titlesCheckBox.addActionListener(new ActionListener(){
    		@Override
			public void actionPerformed(ActionEvent arg0) {
				initTableData(titlesCheckBox.isSelected());
			}
    	});
    	
        //Open Button
        openButton = new JButton(IconLoader.getInstance().getFolderIcon());
        openButton.setToolTipText("Select expression file");
        openButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new TextPlainFileFilter());
                int returnVal = fc.showOpenDialog(GeneListImportDialog.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();

                        ReadFileTask task = new ReadFileTask(file);
                        JTaskConfig config = new JTaskConfig();
                        config.displayCancelButton(true);

                        boolean success = TaskManager.executeTask(task, config);
                        if(success){
                        	data = task.getData();
                        	dsName = task.getName();
                        	initTableData(titlesCheckBox.isSelected());
                        }
                    }
                }
        });

        //North Panel
        {
            JPanel aux = new JPanel();
            aux.add(titlesCheckBox);
            aux.add(openButton);

            //fomat list
            //Item[] items = PluginProperties.getInstance().getGeneIdFormats();
            panel.add(aux, BorderLayout.NORTH);
        }

        //Central Panel
        {
            //Table
            table = new JTable();
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            panel.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        //South Panel
        {
            nextButton = new JButton("Next", IconLoader.getInstance().getForwardIcon());
            nextButton.setToolTipText("Next");
            nextButton.setEnabled(false);
            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

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
                	
                	
                	Task indexTask = new DataSetFileIndexingTask(dsName, table.getModel());
                	
                	JTaskConfig config = new JTaskConfig();
					boolean success = TaskManager.executeTask(indexTask, config);
					if(success){
						GUIManager.getInstance().setVisibleDialog(Dialogs.DATASET);
					}else{
						JOptionPane.showMessageDialog(GeneListImportDialog.this, "There was an error while indexing the file", "Indexing error", JOptionPane.ERROR_MESSAGE);
					}
                }
            });


            JPanel south = new JPanel();
            south.add(nextButton);

            panel.add(south, BorderLayout.SOUTH);
            setContentPane(panel);
        }
    }
    
    private void initTableData(boolean hasTitles){
    	if (data.size() > 0) {
    		TableModel model;
			if (titlesCheckBox.isSelected()) {
				Vector<Object> tableData = new Vector<Object>(data);
				tableData.remove(0);
				model = new DefaultTableModel(tableData,data.elementAt(0));

			}else {
				Vector<String> titles = new Vector<String>();
				titles.add("Gene Id(s)");
				for (int i = 1; i < data.get(0).size(); i++) {
					titles.add("Condition_" + (i));
				}
				model = new DefaultTableModel(data, titles);
			}
			
			table.setModel(model);
			nextButton.setEnabled(true);
			getContentPane().repaint();
		}
    }  
}
