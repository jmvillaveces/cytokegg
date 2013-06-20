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
package age.mpi.de.cytokegg.internal.task;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.model.DataSet;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.ui.UIManager;
import age.mpi.de.cytokegg.internal.util.PathwayItem;

public class PathwaySearchTask implements Task{

	//Task variables
    private final String TASK_TITLE = "Looking for pathways";
	
    public PathwaySearchTask(){
    }

	@Override
	public void run(TaskMonitor taskMonitor) {
		
		// Give the task a title.
        taskMonitor.setTitle(TASK_TITLE);
		taskMonitor.setProgress(-1);
		
		try {
			DataSet geneLst = CKController.getInstance().getCurrentDataSet();
			
			List<String> genes = new ArrayList<String>(); 
			for(String uId : geneLst.getGenes()){
				
				if(!uId.equals(""))
					genes.add(uId);
			}
			PathwayItem[] pathways = Repository.getInstance().getPathwaysByGenes(genes);
			CKController.getInstance().setResultPathways(pathways);
			UIManager.getInstance().update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}

	@Override
	public void cancel() {
	}
}
