/*
 * Copyright (C) 2011-2012 José María Villaveces Max Planck institute for
 * biology of ageing (MPI-age)
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.lucene.index.CorruptIndexException;
import org.cytoscape.work.TaskIterator;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.task.DataSetFileIndexingTask;
import age.mpi.de.cytokegg.internal.task.ReadFileTask;
import age.mpi.de.cytokegg.internal.util.IconLoader;
import age.mpi.de.cytokegg.internal.util.TextPlainFileFilter;

public class GeneListImportDialog extends JDialog{
	
	private JCheckBox titlesCheckBox;
    private JTable table;
    private JButton openButton, nextButton;
    private Vector<Vector> data;
	protected String dsName;
    
    public GeneListImportDialog(JDialog owner){
    	super(owner, "File Import", true);
    	
    	this.setSize(new Dimension(400, 400));
    	
    	
    	
    	JPanel panel = new JPanel(new BorderLayout());
    	panel.setBorder(new TitledBorder("File import"));
    	
    	//Titles CheckBox
    	titlesCheckBox = new JCheckBox("Include Titles", false);
    	titlesCheckBox.addActionListener(new ActionListener(){
    		@Override
			public void actionPerformed(ActionEvent arg0) {
				initTableData();
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
                        
                    ReadFileTask task = new ReadFileTask(file, GeneListImportDialog.this);
                    CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(task));
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
                	
                	GeneListImportDialog.this.setVisible(false);
                	try {
            			if(Repository.getInstance().isDataSetIndexed(dsName)){
            				int answer = JOptionPane.showConfirmDialog(null, "It seems like the dataset "+dsName+" already exists. Do you want to index it again?");
            				if(answer == JOptionPane.YES_OPTION){
            					Repository.getInstance().deleteDataset(dsName);
            				}else{
            					return;
            				}
            			}
            		} catch (CorruptIndexException e) {
            			e.printStackTrace();
            		} catch (IOException e1) {
            			e1.printStackTrace();
            		}
                	
                	DataSetFileIndexingTask task = new DataSetFileIndexingTask(dsName, table.getModel());
                	CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(task));
                }
            });


            JPanel south = new JPanel();
            south.add(nextButton);

            panel.add(south, BorderLayout.SOUTH);
            setContentPane(panel);
        }
    }
    
    public void initTableData(){
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

	public Vector<Vector> getData() {
		return data;
	}

	public void setData(Vector<Vector> data) {
		this.data = data;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
}
