package de.mpg.biochem.cytokegg.internal.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;

import de.mpg.biochem.cytokegg.internal.Pathway;
import de.mpg.biochem.cytokegg.internal.service.KeggService;
import de.mpg.biochem.cytokegg.internal.util.Item;

public class LoopOverPathwaysTask implements ObservableTask {

	//Task variables
    private final String TASK_TITLE = "Looking for pathways";
	
	private String organism;
	private String targetColumn;
	private CyTable nodeTable;
	private Set<Pathway> pathways;
	
	private boolean cancel;
	
	public LoopOverPathwaysTask(String organism, String targetColumn, CyTable nodeTable) {
		this.organism = organism;
		this.targetColumn = targetColumn;
		this.nodeTable = nodeTable;
	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		
		cancel = false;
		
		// Give the task a title.
        taskMonitor.setTitle(TASK_TITLE);
		taskMonitor.setProgress(-1);
		
		KeggService kegg = KeggService.getInstance();
		List<Item> pathwayItems = kegg.getPathwaysByOrg(organism);
		Set<String> colValues = new HashSet<String>(nodeTable.getColumn(targetColumn).getValues(String.class));
		pathways = new HashSet<Pathway>();
		
		for(Item item : pathwayItems){
			
			if(cancel){
				break;
			}
		
			Pathway pathway = kegg.getPathway(item.getId());
			
			taskMonitor.setStatusMessage("Inspecting pathway - " + pathway.getName());
			
			Set<String> intersection = new HashSet<String>(colValues);
			intersection.retainAll(pathway.getGenes());
			
			if(intersection.size() > 0){
				pathway.setGenesInNetwork(intersection);
				pathways.add(pathway);
			}
		}
	}

	@Override
	public void cancel() {
		cancel = true;
	}

	@Override
	public <R> R getResults(Class<? extends R> arg0) {
		return (R) pathways;
	}

}
