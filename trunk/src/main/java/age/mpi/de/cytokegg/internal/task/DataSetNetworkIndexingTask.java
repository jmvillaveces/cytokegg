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
package age.mpi.de.cytokegg.internal.task;

import java.io.File;
import java.util.List;

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
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.repository.RepositoryFields;
import age.mpi.de.cytokegg.internal.ui.UIManager;
import age.mpi.de.cytokegg.internal.util.PluginProperties;

public class DataSetNetworkIndexingTask implements Task{
	//Task variables
    private final String TASK_TITLE = "Building data set";
    
    private Version lVersion = Version.LUCENE_43;

    private String idAttr;
    private List<String> conditions; 
    private CyNetwork network;
	private String dsetName;
	
	//Logger
    private Logger logger = LoggerFactory.getLogger("CyUserMessages");
	
    public DataSetNetworkIndexingTask(String dsetName, String idAttr, List<String> conditions, CyNetwork network){
    	this.dsetName = dsetName;
    	this.idAttr = idAttr;
    	this.conditions = conditions;
    	this.network = network;
    }
    
	@Override
	public void run(TaskMonitor taskMonitor) {
		
		// Give the task a title.
        taskMonitor.setTitle(TASK_TITLE);
		taskMonitor.setProgress(-1);
		
		try{
			double min = 0;
			double max = 0;
			
			int mapped = 0;
			
			Document dataSet = new Document();
	    	dataSet.add(new StringField(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag(), Field.Store.YES));
	    	dataSet.add(new StringField(RepositoryFields.TITLE.getTag(), dsetName, Field.Store.YES));
			
			List<CyNode> nodes = network.getNodeList();//.getNodeIndicesArray();
			CyTable nodeAtt = network.getDefaultNodeTable();
			
			boolean flag = false;
			for(int i=0; i<nodes.size(); i++){
				
				taskMonitor.setStatusMessage("Indexing gene "+i+" of "+nodes.size());
				
				CyRow row = nodeAtt.getRow(nodes.get(i).getSUID());
				
				String uId = row.get(idAttr, String.class);
				if(uId == null || uId.equals(""))
					continue;
				
				String keggId = Repository.getInstance().getKeggId(uId);
				if(!keggId.equals("")){
					mapped++;
					
					dataSet.add(new StringField(RepositoryFields.GENE.getTag(), keggId, Field.Store.YES));
					for(String condition : conditions){
						
						double expression = row.get(condition, Double.class);
						dataSet.add(new TextField(RepositoryFields.EXPRESSION.getTag(), expression+"", Field.Store.YES));
						
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
			
			taskMonitor.setStatusMessage("Indexing dataset conditions");
			for(String condition : conditions){
				dataSet.add(new TextField(RepositoryFields.CONDITION.getTag(), condition, Field.Store.YES));
			}
			
			IndexWriterConfig config = new IndexWriterConfig(lVersion, new StandardAnalyzer(lVersion));
			IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(PluginProperties.getInstance().getIndexPath())), config);
			indexWriter.addDocument(dataSet);
			indexWriter.commit();
			indexWriter.close();
			
			Repository.getInstance().initSearcher();
			logger.info(PluginProperties.getInstance().getPluginName() + " mapped "+ mapped + " genes out of "+nodes.size());
			UIManager.getInstance().update();
		}catch(Exception e){
			logger.error("There was an error while indexing the dataset.", e);
			e.printStackTrace();
			//JOptionPane.showMessageDialog(UIManager.getInstance().getReference(), "There was an error while indexing the dataset.", "Indexing error", JOptionPane.ERROR_MESSAGE);
		}
		return;
	}

	@Override
	public void cancel() {
		try {
			Repository.getInstance().deleteDataset(dsetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
