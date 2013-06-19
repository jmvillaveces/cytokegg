package age.mpi.de.cytokegg.internal.task;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.repository.RepositoryFields;
import age.mpi.de.cytokegg.internal.service.KeggService;
import age.mpi.de.cytokegg.internal.util.Item;
import age.mpi.de.cytokegg.internal.util.PluginProperties;


public class IndexBuilderTask extends AbstractTask{

	private IndexWriter indexWriter;
	private Version lVersion = Version.LUCENE_43;
	private KeggService service;
	private Item[] orgs;
	
	private int maxThreads = PluginProperties.getInstance().getMaxThreads();
	private int availableThreads = maxThreads;
	
	//Task variables
    private boolean interrupted = false;
    private final String TASK_TITLE = "Creating index";
    
    //Logger
    private Logger logger = LoggerFactory.getLogger("CyUserMessages");
    
    public IndexBuilderTask(Item[] orgs) throws IOException{
		service = KeggService.getInstance();
		this.orgs = orgs;
	}

	@Override
	public void run(TaskMonitor taskMonitor){
		try{
			// Give the task a title.
	        taskMonitor.setTitle(TASK_TITLE);
			taskMonitor.setProgress(-1);
	        
			IndexWriterConfig config = new IndexWriterConfig(lVersion, new StandardAnalyzer(lVersion));
			indexWriter = new IndexWriter(FSDirectory.open(new File(PluginProperties.getInstance().getIndexPath())), config);
			
			for(int i = 0; i<orgs.length; i++){
				
				//Indexing Organism
				Document org = new Document();
				org.add(new TextField(RepositoryFields.TYPE.getTag(), RepositoryFields.ORGANISM.getTag(), Field.Store.YES));
				org.add(new TextField(RepositoryFields.ORGANISM.getTag(), orgs[i].getId(), Field.Store.YES));
				org.add(new TextField(RepositoryFields.ORGANISM_DESC.getTag(), orgs[i].getDescription(), Field.Store.YES));
				indexWriter.addDocument(org);
				
				List<Item> paths = service.getPathwaysByOrg(orgs[i].getId());
				
				int count = 0;
				for(Item path : paths){
					while(!interrupted){
						if(availableThreads > 0){
							availableThreads--;
	
							String msg = "Indexing Organism ["+(i+1)+"/"+orgs.length+"]: "+orgs[i]+" , pathway "+(count+1)+"/"+paths.size();
							taskMonitor.setStatusMessage(msg);
							
							Thread runner = new Thread(new PathwayInfo(path, orgs[i]), path.getId());
							runner.start();
							count ++;
							break;
						} else{
							Thread.sleep(500);
						}
					}
				}
			}
			
			indexWriter.commit();
			indexWriter.close();
			if(interrupted){
				interrupt();
			}
			
			Repository.getInstance().initSearcher();
			logger.info("Inedexed "+orgs.length+" organisms.");
		}catch(Exception e){
			logger.error("There was an error while writing to the index ", e);
			e.printStackTrace();
		}
		return;
	}
	
	public void cancel() {
		interrupted = true;
	}
	
	private void interrupt() throws  IOException{
		insertTasksAfterCurrentTask(new TaskIterator(new AbstractTask(){
			@Override
			public void run(TaskMonitor taskMonitor) throws Exception {
				taskMonitor.setTitle("Deleting Index");
				taskMonitor.setProgress(-1);
				
				Repository.getInstance().deleteIndex();
				
				logger.info("Index deleted.");
			}	
		}));
	}
	
	/**
	 * This thread fetches the pathway information
	 */
	class PathwayInfo implements Runnable{
		
		private Item pathway;
		private Item organism;
			
		public PathwayInfo(Item pathway, Item organism){
			this.pathway = pathway;
			this.organism = organism;
		}
			
		@Override
		public void run() {
			try{
				Map<String, List<String>> genes = mapIds(service.getGenesByPathway(pathway.getId()), organism.getId());
					
				//IndexPathway
				Document path = new Document();
					
				path.add(new TextField(RepositoryFields.TYPE.getTag(), RepositoryFields.PATHWAY.getTag(), Field.Store.YES));
				path.add(new TextField(RepositoryFields.ID.getTag(), pathway.getId(), Field.Store.YES));
				path.add(new TextField(RepositoryFields.TITLE.getTag(), pathway.getDescription(), Field.Store.YES));
				path.add(new TextField(RepositoryFields.ORGANISM_DESC.getTag(), organism.getDescription(), Field.Store.YES));
				path.add(new TextField(RepositoryFields.ORGANISM_ID.getTag(), organism.getId(), Field.Store.YES));
					
				for(String geneId : genes.keySet()){
					
					//Add gene id to pathway
					path.add(new TextField(RepositoryFields.GENE.getTag(), geneId, Field.Store.YES));
					//path.add(new TextField(RepositoryFields.GENE.getTag(),  getId(geneId), Field.Store.YES));
						
					//Update Gene
					Document gene = new Document();
					gene.add(new StringField(RepositoryFields.TYPE.getTag(), RepositoryFields.GENE.getTag(), Field.Store.YES));
					
					//gene.add(new TextField(RepositoryFields.ID.getTag(), getId(geneId), Field.Store.YES));
					gene.add(new TextField(RepositoryFields.ID.getTag(), geneId, Field.Store.YES));
					
					for(String altId : genes.get(geneId)){
						gene.add(new TextField(RepositoryFields.ALT_ID.getTag(), getId(altId), Field.Store.YES));
						gene.add(new TextField(RepositoryFields.ALT_ID.getTag(), altId, Field.Store.YES));
					}
						
					gene.add(new TextField(RepositoryFields.ALT_ID.getTag(), getId(geneId), Field.Store.YES));
					gene.add(new TextField(RepositoryFields.ALT_ID.getTag(), geneId, Field.Store.YES));
						
					indexWriter.updateDocument(new Term(RepositoryFields.ID.getTag(), geneId), gene);
				}
				indexWriter.addDocument(path);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				availableThreads++;
			}
		} 
			
		private Map<String, List<String>> mapIds(List<Item> genes, String org){
				
			if(genes.size()>0){
				String[] genesArr = new String[genes.size()];
				int i = 0;
				for(Item it : genes){
					genesArr[i] = org+":"+it.getId();
					i++;
				}
					
				String[] targets = new String[]{KeggService.getInstance().NCBI_GENE_ID, KeggService.getInstance().UNIPROT, KeggService.getInstance().NCBI_GI};
				return KeggService.getInstance().mapIds(targets, genesArr);
			}
			return new HashMap<String, List<String>>();
		}
		
		private String getId(String id){
			if(id.contains(":"))
				return id.substring(id.indexOf(":")+1);
			
			return id;
		}
	}
}
