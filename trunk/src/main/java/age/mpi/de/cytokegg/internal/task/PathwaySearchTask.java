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
			if(geneLst == null){
				System.out.println("datset is null");
			}
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
