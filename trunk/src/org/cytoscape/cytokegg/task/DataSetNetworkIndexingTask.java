package org.cytoscape.cytokegg.task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.RepositoryFields;
import org.cytoscape.cytokegg.util.PluginProperties;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class DataSetNetworkIndexingTask implements Task{
	//Task variables
    private boolean interrupted = false;
    private TaskMonitor taskMonitor;
    private final String TASK_TITLE = "Building data set";

    private String idAttr;
    private List<String> conditions; 
    private CyNetwork network;
	private String dsetName;
    
    public DataSetNetworkIndexingTask(String dsetName, String idAttr, List<String> conditions, CyNetwork network){
    	this.dsetName = dsetName;
    	this.idAttr = idAttr;
    	this.conditions = conditions;
    	this.network = network;
    }
    
	@Override
	public void run() {
		
		double min = 0;
		double max = 0;
		
		int mapped = 0;
		
		Document dataSet = new Document();
    	dataSet.add(new Field(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag(), Field.Store.YES, Field.Index.NOT_ANALYZED));
    	dataSet.add(new Field(RepositoryFields.TITLE.getTag(), dsetName, Field.Store.YES, Field.Index.NOT_ANALYZED));
		
		int[] nodeInd = network.getNodeIndicesArray();
		CyAttributes nodeAtt = Cytoscape.getNodeAttributes();
		
		boolean flag = false;
		for(int nInd : nodeInd){
			
			if(interrupted)
				return;
			
			try {
				String node = network.getNode(nInd).getIdentifier();
				String uId = nodeAtt.getStringAttribute(node, idAttr);
				String keggId = Repository.getInstance().getKeggId(uId);
				
				if(!keggId.equals("")){
					mapped++;
					
					for(String condition : conditions){
						
						if(interrupted)
							return;
						
						double expression = Double.parseDouble(nodeAtt.getAttribute(node, condition).toString());
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
    			JOptionPane.showMessageDialog(null, PluginProperties.getInstance().getPluginName() + " mapped "+ mapped + " genes out of "+nodeInd.length);
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
