package age.mpi.de.cytokegg.internal.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
		int count =0;
		Iterator<CyRow> i = nodeTable.getMatchingRows("KEGG.entry", "gene").iterator();
		while(i.hasNext()){
			CyRow row = i.next();
			String[] names = row.get("KEGG.name", String.class).split(" ");
			String gene = getGeneInDataSet(names, genes);
			if(!gene.equals("")){
				row.set("expression", Arrays.asList(dataSet.getExpression(gene)));
				row.set("hasExpression", true);
				count ++;
				
				System.out.println(gene +" is in dataset");
			}
		}
		System.out.println(count);
		
		i = nodeTable.getMatchingRows("KEGG.entry", "ortholog").iterator();
		while(i.hasNext()){
			CyRow row = i.next();
			String[] names = row.get("KEGG.name", String.class).split(" ");
			String gene = getGeneInDataSet(names, genes);
			if(!gene.equals("")){
				row.set("expression", Arrays.asList(dataSet.getExpression(gene)));
				row.set("hasExpression", true);
			}
		}
	}
	
	public String getGeneInDataSet(String[] geneNames, String[] dataSetGenes){
		for(int i=0; i<dataSetGenes.length; i++){
			for(int j=0; j<geneNames.length; j++){
				if(dataSetGenes[i].equalsIgnoreCase(geneNames[j]))
					return dataSetGenes[i];
			}
		}
		return "";
	}
}
