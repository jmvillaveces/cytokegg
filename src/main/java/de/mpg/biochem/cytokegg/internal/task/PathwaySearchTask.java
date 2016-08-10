package de.mpg.biochem.cytokegg.internal.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;

import de.mpg.biochem.cytokegg.internal.service.KeggService;
import de.mpg.biochem.cytokegg.internal.util.Item;

public class PathwaySearchTask implements ObservableTask {
	
	//Task variables
    private final String TASK_TITLE = "Looking for pathways";
    
    private List<String> orgs;
    private String query;
    private List<Item> results;
	
    private Logger logger = Logger.getLogger(this.getClass());
    
    public PathwaySearchTask(){}
	
	@Override
	public void cancel() {
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		// Give the task a title.
        taskMonitor.setTitle(TASK_TITLE);
		taskMonitor.setProgress(-1);
		
		results = new ArrayList<Item>();
		for(String org : orgs){
		
			logger.info("Seraching pathways for :" + org);
			List<Item> paths = KeggService.getInstance().getPathwaysByOrg(org);
			for(Item path : paths){
				if(path.getDescription().matches("(?i)(" + query + ").*")){
					results.add(path);
				}
			}
			
		}
	}
	
	public void setOrg(String org){
		orgs = new ArrayList<String>();
		orgs.add(org);
	}
	
	public void setOrgs(List<String> orgs){
		this.orgs = orgs;
	}
	
	public void setQuery(String query){
		this.query = query;
	}

	@Override
	public <R> R getResults(Class<? extends R> type) {
		return (R) results;
	}

}
