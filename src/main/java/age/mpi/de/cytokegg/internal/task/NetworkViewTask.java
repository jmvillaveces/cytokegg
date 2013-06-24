/*
 * Copyright (C) 2011-2012 José María Villaveces Max Planck institute for
 * biology of ageing (MPI-age)
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
