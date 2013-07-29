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

import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import age.mpi.de.cytokegg.internal.CKController;

public class NetworkViewTask extends AbstractTask{

	private List<CySubNetwork> nets;

	public NetworkViewTask(List<CySubNetwork> list){
		this.nets = list;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		List<CyNetworkView> views = new ArrayList<CyNetworkView>();
		taskMonitor.setStatusMessage("Creating network view ...");
		for(int i=0; i<nets.size(); i++){
			CyNetworkView myView = CKController.getInstance().getNetworkViewFactory().createNetworkView(nets.get(i));
			// Add view to Cytoscape
			CKController.getInstance().getNetworkViewManager().addNetworkView(myView);
			views.add(myView);
		}
		
		TaskIterator ti = new TaskIterator(new LoadVisualPropertiesTask(views));
		this.insertTasksAfterCurrentTask(ti);
	}

}
