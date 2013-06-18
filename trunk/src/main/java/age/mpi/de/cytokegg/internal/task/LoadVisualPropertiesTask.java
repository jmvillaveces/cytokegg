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