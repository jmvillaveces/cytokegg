package age.mpi.de.cytokegg.internal.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.model.DataSet;

public class NetworkExpresionAnnotationTask extends AbstractTask{

	private CyRootNetwork rootNet;
	
	public NetworkExpresionAnnotationTask(CyRootNetwork rootNet){
		this.rootNet = rootNet;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Expression Annotation");
		taskMonitor.setProgress(-1);
		taskMonitor.setStatusMessage("Annotating network with expression data ...");
		
		DataSet dataSet = CKController.getInstance().getCurrentDataSet();
		
		String[] conditions = dataSet.getConditions();
		CyTable netTable = rootNet.getDefaultNetworkTable();
		netTable.createListColumn("conditions", String.class, false);
		netTable.createColumn("min", Double.class, false);
		netTable.createColumn("max", Double.class, false);
		
		CyRow netRow = netTable.getRow(rootNet.getSUID());
		netRow.set("conditions", Arrays.asList(conditions));
		netRow.set("min", dataSet.getMin());
		netRow.set("max", dataSet.getMax());
		
		CyTable nodeTable = rootNet.getDefaultNodeTable();
		nodeTable.createListColumn("expression", Double.class, false);
		nodeTable.createColumn("hasExpression", Boolean.class, false);
		
		String[] genes = dataSet.getGenes();
		for(String gene : genes){
			Collection<CyRow> matchingRows = nodeTable.getMatchingRows("KEGG.name", gene);
			Iterator<CyRow> i = matchingRows.iterator();
			while(i.hasNext()){
				CyRow row = i.next();
				row.set("expression", Arrays.asList(dataSet.getExpression(gene)));
				row.set("hasExpression", true);
			}
		}
	}

}
