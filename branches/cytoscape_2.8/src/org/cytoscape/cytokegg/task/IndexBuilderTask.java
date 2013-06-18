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
package org.cytoscape.cytokegg.task;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.RepositoryFields;
import org.cytoscape.cytokegg.kegg.rest.KeggService;
import org.cytoscape.cytokegg.util.Item;
import org.cytoscape.cytokegg.util.PluginProperties;

import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class IndexBuilderTask implements Task{
	
	private CyLogger logger = CyLogger.getLogger();
	
	private Item[] orgs;
	private int maxThreads = PluginProperties.getInstance().getMaxThreads();
	private int availableThreads = maxThreads;
	
	//Index Writer
	private IndexWriter indexWriter;
	
	//Task variables
    private boolean interrupted = false;
    private TaskMonitor taskMonitor;
    private final String TASK_TITLE = "Creating index";

	private KeggService service;
	
	public IndexBuilderTask(Item[] orgs) throws IOException{
		service = KeggService.getInstance();
		this.orgs = orgs;
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

    @Override
    public String getTitle() {
        return TASK_TITLE;
    }
    
    @Override
	public void run() {
		try{
			indexWriter = new IndexWriter(FSDirectory.open(new File(PluginProperties.getInstance().getIndexPath())),new StandardAnalyzer(Version.LUCENE_30), !Repository.getInstance().exists(), IndexWriter.MaxFieldLength.UNLIMITED);
			
			for(int i = 0; i<orgs.length; i++){
				logger.info("Indexing organism ["+orgs[i]+"],"+(i+1)+" of "+orgs.length);
				
				//Indexing Organism
				Document org = new Document();
				org.add(new Field(RepositoryFields.TYPE.getTag(), RepositoryFields.ORGANISM.getTag(), Field.Store.YES, Field.Index.ANALYZED));
				org.add(new Field(RepositoryFields.ORGANISM.getTag(), orgs[i].getId(), Field.Store.YES, Field.Index.ANALYZED));
				org.add(new Field(RepositoryFields.ORGANISM_DESC.getTag(), orgs[i].getDescription(), Field.Store.YES, Field.Index.ANALYZED));
				indexWriter.addDocument(org);
				
				List<Item> paths = service.getPathwaysByOrg(orgs[i].getId());
				
				int count = 0;
				for(Item path : paths){
					while(!interrupted){
						if(availableThreads > 0){
							availableThreads--;

							String msg = "Indexing Organism ["+(i+1)+"/"+orgs.length+"]: "+orgs[i]+" , pathway "+(count+1)+"/"+paths.size();
							logger.info(msg);
								
							taskMonitor.setStatus(msg);
							
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
	    	while(availableThreads<maxThreads){
				Thread.sleep(500);
			}
	    	
	    	indexWriter.commit();
		}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try {
    			indexWriter.optimize();
				indexWriter.close(true);
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
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
				
				path.add(new Field(RepositoryFields.TYPE.getTag(), RepositoryFields.PATHWAY.getTag(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				path.add(new Field(RepositoryFields.ID.getTag(), pathway.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				path.add(new Field(RepositoryFields.TITLE.getTag(), pathway.getDescription(), Field.Store.YES, Field.Index.ANALYZED));
				path.add(new Field(RepositoryFields.ORGANISM_DESC.getTag(), organism.getDescription(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				path.add(new Field(RepositoryFields.ORGANISM_ID.getTag(), organism.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				
				for(String geneId : genes.keySet()){
					
					//Add gene id to pathway
					path.add(new Field(RepositoryFields.GENE.getTag(), geneId, Field.Store.YES, Field.Index.ANALYZED));
					path.add(new Field(RepositoryFields.GENE.getTag(), geneId, Field.Store.YES, Field.Index.NOT_ANALYZED));
					
					//Update Gene
					Document gene = new Document();
					gene.add(new Field(RepositoryFields.TYPE.getTag(), RepositoryFields.GENE.getTag(), Field.Store.YES, Field.Index.NOT_ANALYZED));
					gene.add(new Field(RepositoryFields.ID.getTag(), geneId, Field.Store.YES, Field.Index.NOT_ANALYZED));
					gene.add(new Field(RepositoryFields.ID.getTag(), geneId, Field.Store.YES, Field.Index.ANALYZED));
					
					for(String altId : genes.get(geneId)){
						gene.add(new Field(RepositoryFields.ALT_ID.getTag(), altId, Field.Store.YES, Index.NOT_ANALYZED));
						gene.add(new Field(RepositoryFields.ALT_ID.getTag(), altId, Field.Store.YES, Index.ANALYZED));
					}
					
					gene.add(new Field(RepositoryFields.ALT_ID.getTag(), geneId, Field.Store.YES, Index.NOT_ANALYZED));
					gene.add(new Field(RepositoryFields.ALT_ID.getTag(), geneId, Field.Store.YES, Index.ANALYZED));
					
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
	}
}



