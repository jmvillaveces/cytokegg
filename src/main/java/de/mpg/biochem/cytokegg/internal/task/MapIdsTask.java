package de.mpg.biochem.cytokegg.internal.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import de.mpg.biochem.cytokegg.internal.service.KeggService;
import de.mpg.biochem.cytokegg.internal.util.Item;

public class MapIdsTask extends AbstractTask{
	
	//Task variables
    private final String TASK_TITLE = "Looking for pathways";

	private String target;
	private String organism;
	private String sourceColumn;
	private String targetColumn;
	private CyTable nodesTable;
	
	public MapIdsTask(String target, String organism, String sourceColumn, String targetColumn, CyTable nodesTable){
		this.target = target;
		this.organism = organism;
		this.nodesTable = nodesTable;
		this.sourceColumn = sourceColumn;
		this.targetColumn = targetColumn;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		// Give the task a title.
        taskMonitor.setTitle(TASK_TITLE);
		taskMonitor.setProgress(-1);
		
		taskMonitor.setStatusMessage("Mapping identifiers");
		
		Map<String, String> map = createIdHashMap(organism, target);
		
		CyColumn idCol = nodesTable.getColumn(targetColumn);
		if(idCol == null){
			nodesTable.createColumn(targetColumn, String.class, false);
			idCol = nodesTable.getColumn(targetColumn);
		}
		
		Iterator<CyRow> iterator = nodesTable.getAllRows().iterator();
		while(iterator.hasNext()){
			CyRow row = iterator.next();
			String sourceId = row.get(sourceColumn, String.class);
			
			if(map.containsKey(sourceId)){
				row.set(targetColumn, map.get(sourceId));
			}
		}
	}
	
	private Map<String, String> createIdHashMap(String organism, String target) throws IOException{
		List<Item> ids = KeggService.getInstance().getOrganismIds(organism, target);
		
		Map<String, String> map = new HashMap<String, String>();
		
		Iterator<Item> i = ids.iterator();
		while(i.hasNext()){
			
			Item item = i.next();
			
			String sourceId = item.getId().split(":")[1];
			String targetId = item.getDescription().split(":")[1];
			
			
			map.put(sourceId, targetId);
		}
		
		return map;
	}

}
