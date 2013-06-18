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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.cytoscape.cytokegg.DataSet;
import org.cytoscape.cytokegg.Plugin;
import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.util.PathwayItem;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class PathwaySearchTask implements Task{

	//Task variables
    private boolean interrupted = false;
    private TaskMonitor taskMonitor;
    private final String TASK_TITLE = "Looking for pathways";
	
	private PathwayItem[] pathways;
	
    public PathwaySearchTask(){
    }

	@Override
	public void run() {
		try {
			DataSet geneLst = Plugin.getInstance().getCurrentDataSet();
			
			List<String> genes = new ArrayList<String>(); 
			for(String uId : geneLst.getGenes()){
				
				if(!uId.equals(""))
					genes.add(uId);
			}
			pathways = Repository.getInstance().getPathwaysByGenes(genes);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
    
	public PathwayItem[] getPathways(){
		return pathways;
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
