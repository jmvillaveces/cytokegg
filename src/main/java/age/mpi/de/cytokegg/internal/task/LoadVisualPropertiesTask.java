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

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import age.mpi.de.cytokegg.internal.CKController;

public class LoadVisualPropertiesTask extends AbstractTask {

	private List<CyNetworkView> views;

	public LoadVisualPropertiesTask(List<CyNetworkView> views) {
		this.views = views;
	}

	@Override
	public void run(TaskMonitor taskMonitor) {

		taskMonitor.setStatusMessage("Loading visual style from file ...");
		
		InputStream is = this.getClass().getResourceAsStream("/vizmap.xml");
		Set<VisualStyle> vsSet = CKController.getInstance().getLoadVizmapFileTaskFactory().loadStyles(is);

		if (vsSet.size() == 0)
			return;
		
		VisualStyle style = vsSet.iterator().next();
		for(int i=0; i<views.size(); i++){
			CyNetworkView view = views.get(i);
			style.apply(view);
			view.updateView();
		}
	}
}