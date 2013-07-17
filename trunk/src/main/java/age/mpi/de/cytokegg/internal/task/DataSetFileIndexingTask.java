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
package age.mpi.de.cytokegg.internal.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.repository.RepositoryFields;
import age.mpi.de.cytokegg.internal.ui.UIManager;
import age.mpi.de.cytokegg.internal.util.PluginProperties;

public class DataSetFileIndexingTask extends AbstractTask{

	//Task variables
    private final String TASK_TITLE = "Building data set";
	
    private String dsetName;
	private TableModel model;
	private Version lVersion = Version.LUCENE_43;
	
	//Logger
    private Logger logger = LoggerFactory.getLogger("CyUserMessages");
	
	public DataSetFileIndexingTask(String dsetName, TableModel model){
    	this.dsetName = dsetName;
    	this.model = model;
    }
    

	@Override
	public void run(TaskMonitor taskMonitor) {
		
		// Give the task a title.
        taskMonitor.setTitle(TASK_TITLE);
		taskMonitor.setProgress(-1);
		
		double min = 0;
		double max = 0;
		int mapped = 0;
		
		int cols = model.getColumnCount();
    	int rows = model.getRowCount();
    	
    	Document dataSet = new Document();
    	dataSet.add(new StringField(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag(), Field.Store.YES));
    	dataSet.add(new StringField(RepositoryFields.TITLE.getTag(), dsetName, Field.Store.YES));
    	
    	
    	List<String> conditions = new ArrayList<String>();
    	boolean flag = false;
    	
    	try {
    		for(int i=0; i<rows; i++){
	    		String uId = model.getValueAt(i, 0).toString();
	    		if(uId == null)
					continue;
	    		
	    		String keggId = Repository.getInstance().getKeggId(uId);
				
				taskMonitor.setStatusMessage(PluginProperties.getInstance().getPluginName() + " mapped "+ mapped + " genes out of "+rows);
				
				if(!keggId.equals("")){
					mapped++;
					
					dataSet.add(new StringField(RepositoryFields.GENE.getTag(), keggId, Field.Store.YES));
					for(int j=1; j<cols; j++){
								
						if(conditions.size()<cols-1)
			        		conditions.add(model.getColumnName(j));
								
						double expression = Double.parseDouble(model.getValueAt(i, j).toString());
						dataSet.add(new DoubleField(RepositoryFields.EXPRESSION.getTag(), expression, Field.Store.YES));
						
						if(!flag){
							min = expression;
							max = expression;
							flag = true;
						}
							
						if(expression < min){
							min = expression;
						}else if(expression > max){
							max = expression;
						}
					}
				}
    		}
    			
    		dataSet.add(new DoubleField(RepositoryFields.MIN.getTag(), min, Field.Store.YES));
		    dataSet.add(new DoubleField(RepositoryFields.MAX.getTag(), max, Field.Store.YES));
		    
		    for(String condition : conditions){
				dataSet.add(new TextField(RepositoryFields.CONDITION.getTag(), condition, Field.Store.YES));
			}
    		
		    IndexWriterConfig config = new IndexWriterConfig(lVersion, new StandardAnalyzer(lVersion));
		    IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(PluginProperties.getInstance().getIndexPath())), config);
			indexWriter.addDocument(dataSet);
			indexWriter.commit();
			indexWriter.close();
	
			
			Repository.getInstance().initSearcher();
			logger.info(PluginProperties.getInstance().getPluginName() + " mapped "+ mapped + " genes out of "+rows);
			UIManager.getInstance().update();
			
    	} catch (Exception e) {
    		//JOptionPane.showMessageDialog(UIManager.getInstance().getReference(), "There was an error while indexing the file", "Indexing error", JOptionPane.ERROR_MESSAGE);
    		logger.error("There was an error while indexing the dataset.", e);
    		e.printStackTrace();
		}
    	return;
	}

	@Override
	public void cancel() {
		
		Task utilTask = new AbstractTask(){

			@Override
			public void run(TaskMonitor arg0) throws Exception {
				Repository.getInstance().deleteDataset(dsetName);
			}
		};
		
		this.insertTasksAfterCurrentTask(new TaskIterator(utilTask));
	}
}
