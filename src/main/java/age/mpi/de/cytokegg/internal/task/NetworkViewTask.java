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
