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