package age.mpi.de.cytokegg.internal.task;

import java.util.Collection;
import java.util.Iterator;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import age.mpi.de.cytokegg.internal.CKController;

public class SelectionTask implements Task{

	private String selection;
	private long networkId;
	
	public SelectionTask(long networkId, String selection){
		this.selection = selection;
		this.networkId = networkId;
	}
	
	@Override
	public void cancel() {
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Selection Task");
		taskMonitor.setProgress(-1);
		
		CyNetwork selectedNet = CKController.getInstance().getNetMgr().getNetwork(networkId);
    	CyRootNetwork rootNet = ((CySubNetwork) CKController.getInstance().getNetMgr().getNetwork(networkId)).getRootNetwork();
    	
    	Collection<CyNetworkView> netViews = CKController.getInstance().getNetworkViewManager().getNetworkViews(selectedNet);
    	CyNetworkView netView = netViews.iterator().next();
    	
    	taskMonitor.setStatusMessage("Clear selection");
    	clearSelection(rootNet, netView);
    	if(!selection.equals("none")){
    		
    		taskMonitor.setStatusMessage("Selecting "+selection+" in the pathway");
    		Collection<CyRow> rows = rootNet.getDefaultEdgeTable().getMatchingRows("KEGG.name", selection);
        	
        	Iterator<CyRow> i = rows.iterator();
        	while(i.hasNext()){
        		CyRow row = i.next();
        		CyEdge edge = rootNet.getEdge(row.get("SUID", Long.class));
        		
        		netView.getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_SELECTED, true);
        		netView.getNodeView(edge.getSource()).setLockedValue(BasicVisualLexicon.NODE_SELECTED, true);
        	}
    	}
    	netView.updateView();
	}

	private void clearSelection(CyRootNetwork rootNet, CyNetworkView netView){
		Collection<CyRow> rows = rootNet.getDefaultEdgeTable().getAllRows();
		
		Iterator<CyRow> i = rows.iterator();
    	while(i.hasNext()){
    		CyRow row = i.next();
    		CyEdge edge = rootNet.getEdge(row.get("SUID", Long.class));
    		
    		netView.getEdgeView(edge).clearValueLock(BasicVisualLexicon.EDGE_SELECTED);
    		netView.getNodeView(edge.getSource()).clearValueLock(BasicVisualLexicon.NODE_SELECTED);
    	}
	}
	
}
