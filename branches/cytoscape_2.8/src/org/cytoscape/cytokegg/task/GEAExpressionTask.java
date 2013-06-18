package org.cytoscape.cytokegg.task;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.cytoscape.cytokegg.kegg.rest.KeggService;
import org.cytoscape.cytokegg.ui.GEASummaryPanel;
import org.cytoscape.cytokegg.data.GEADAS;
import org.cytoscape.cytokegg.data.GeaApi;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class GEAExpressionTask implements Task{
	
	//Task variables
    private boolean interrupted = false;
    private TaskMonitor taskMonitor;
    private final String TASK_TITLE = "Looking for expression profiles";
	
    private String geneId;
    private GEASummaryPanel panel;
    
    public GEAExpressionTask(String geneId){
    	this.geneId = geneId;
    }

    public GEASummaryPanel getPanel(){
    	return panel;
    }
    
	@Override
	public void run() {
		
		try {
		
			String accession = getUniprotAccession(geneId);
			
			if(accession.equals("") || interrupted)
				return;
			
			GeaApi gApi = new GeaApi();
			gApi.getSummary(accession);
			
			String ensembl=gApi.getEnsemblGeneId();
			
			if(ensembl.equals("") || interrupted)
				return;
			
			GEADAS geadas = new GEADAS();
			panel = new GEASummaryPanel(geadas.getSummary(ensembl));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	private String getUniprotAccession(String acc) throws RemoteException, ServiceException{
		
		Map<String, List<String>> map = KeggService.getInstance().mapIds(KeggService.getInstance().UNIPROT, new String[]{acc});
		List<String> lst = map.get(acc);
		if(lst != null){
			if(lst.size()>0){
				return lst.get(0);
			}
		}
		return "";
	}
	
    @Override
	public String getTitle() {
		return TASK_TITLE;
	}

	@Override
	public void halt() {
        this.interrupted = true;
		
	}

	@Override
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        this.taskMonitor = taskMonitor;
	}

}
