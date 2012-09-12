package org.cytoscape.cytokegg.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.RepositoryFields;
import org.cytoscape.cytokegg.ui.GUIManager;
import org.cytoscape.cytokegg.ui.GUIManager;
import org.cytoscape.cytokegg.util.PluginProperties;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class DataSetFileIndexingTask implements Task{

	//Task variables
    private boolean interrupted = false;
    private TaskMonitor taskMonitor;
    private final String TASK_TITLE = "Building data set";
	
    private String dsetName;
	private TableModel model;

    public DataSetFileIndexingTask(String dsetName, TableModel model){
    	this.dsetName = dsetName;
    	this.model = model;
    }
    
	@Override
	public void run() {
		
		double min = 0;
		double max = 0;
		int mapped = 0;
		
		int cols = model.getColumnCount();
    	int rows = model.getRowCount();
		
    	Document dataSet = new Document();
    	dataSet.add(new Field(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag(), Field.Store.YES, Field.Index.NOT_ANALYZED));
    	dataSet.add(new Field(RepositoryFields.TITLE.getTag(), dsetName, Field.Store.YES, Field.Index.NOT_ANALYZED));
    	
    	ArrayList<String> conditions = new ArrayList<String>();
    	boolean flag = false;
    	for(int i=0; i<rows; i++){
    		
    		try {
    			String uId = model.getValueAt(i, 0).toString();
				String keggId = Repository.getInstance().getKeggId(uId);
				
				if(interrupted)
					return;
				
				if(!keggId.equals("")){
					mapped++;
					
					dataSet.add(new Field(RepositoryFields.GENE.getTag(), keggId, Field.Store.YES, Field.Index.NOT_ANALYZED));
					for(int j=1; j<cols; j++){
							
						if(interrupted)
							return;
							
						if(conditions.size()<cols-1)
			        		conditions.add(model.getColumnName(j));
							
						double expression = Double.parseDouble(model.getValueAt(i, j).toString());
						dataSet.add(new Field(RepositoryFields.EXPRESSION.getTag(), expression+"", Field.Store.YES, Field.Index.NOT_ANALYZED));
						
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
				
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
       	}
    	
    	dataSet.add(new Field(RepositoryFields.MIN.getTag(), min+"", Field.Store.YES, Field.Index.NOT_ANALYZED));
    	dataSet.add(new Field(RepositoryFields.MAX.getTag(), max+"", Field.Store.YES, Field.Index.NOT_ANALYZED));
    	for(String condition : conditions){
			dataSet.add(new Field(RepositoryFields.CONDITION.getTag(), condition, Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
		
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(FSDirectory.open(new File(PluginProperties.getInstance().getIndexPath())),new StandardAnalyzer(Version.LUCENE_30), !Repository.getInstance().exists(), IndexWriter.MaxFieldLength.UNLIMITED);
			indexWriter.addDocument(dataSet);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
    			if(indexWriter != null){
    				indexWriter.optimize();
					indexWriter.close(true);
    			}
    			JOptionPane.showMessageDialog(null, PluginProperties.getInstance().getPluginName() + " mapped "+ mapped + " genes out of "+rows);
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getTitle() {
		return TASK_TITLE;
	}
	
	@Override
	public void halt() {
		this.interrupted = true;
	}
	
	@Override
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        this.taskMonitor = taskMonitor;
	}

}
