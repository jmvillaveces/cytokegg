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
package age.mpi.de.cytokegg.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.task.visualize.ApplyVisualStyleTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;

import age.mpi.de.cytokegg.internal.model.DataSet;
import age.mpi.de.cytokegg.internal.model.Pathway;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.util.PathwayItem;

public class CKController {

	private static CKController instance = new CKController();
	
	
	private BundleContext context;
	private DialogTaskManager dialogTaskManager;
	private CyApplicationManager cyApplicationManager;
	private CyNetworkManager netMgr;
	private CyNetworkFactory networkFactory;
	private CySwingApplication cytoscapeDesktopService;
	private CyTableFactory cyTableFactory;
	private VisualMappingManager vMappingManager;
	private VisualStyleFactory visualStyleFactory;
	private CyNetworkViewFactory networkViewFactory;
	private CyNetworkViewManager networkViewManager;
	private LoadVizmapFileTaskFactory loadVizmapFileTaskFactory;
	private ApplyVisualStyleTaskFactory applyVisualStyleTaskFactory;
	
	private Map<String, DataSet> dataSetMap;
	//private CSPathwayMapper mapper;
	private Map<String, Pathway> pathways;
	private String currentDS;
	private PathwayItem[] resultPathways = new PathwayItem[0];

	/**
	 * Constructor, private because of singleton
	 */
	private CKController() {
		dataSetMap = new HashMap<String, DataSet>();
		pathways = new HashMap<String, Pathway>();
		currentDS = "";
	}

	/**
	 * Get the current instance
	 * @return CKController
	 */
	public static CKController getInstance() {
		return instance;
	}
	
	public LoadVizmapFileTaskFactory getLoadVizmapFileTaskFactory() {
		return loadVizmapFileTaskFactory;
	}

	public void setLoadVizmapFileTaskFactory(
			LoadVizmapFileTaskFactory loadVizmapFileTaskFactory) {
		this.loadVizmapFileTaskFactory = loadVizmapFileTaskFactory;
	}

	public ApplyVisualStyleTaskFactory getApplyVisualStyleTaskFactory() {
		return applyVisualStyleTaskFactory;
	}

	public void setApplyVisualStyleTaskFactory(
			ApplyVisualStyleTaskFactory applyVisualStyleTaskFactory) {
		this.applyVisualStyleTaskFactory = applyVisualStyleTaskFactory;
	}

	public CyNetworkViewFactory getNetworkViewFactory() {
		return networkViewFactory;
	}

	public void setNetworkViewFactory(CyNetworkViewFactory networkViewFactory) {
		this.networkViewFactory = networkViewFactory;
	}

	public CyNetworkViewManager getNetworkViewManager() {
		return networkViewManager;
	}

	public void setNetworkViewManager(CyNetworkViewManager networkViewManager) {
		this.networkViewManager = networkViewManager;
	}

	public CyTableFactory getCyTableFactory() {
		return cyTableFactory;
	}

	public void setCyTableFactory(CyTableFactory cyTableFactory) {
		this.cyTableFactory = cyTableFactory;
	}

	public VisualMappingManager getvMappingManager() {
		return vMappingManager;
	}

	public void setvMappingManager(VisualMappingManager vMappingManager) {
		this.vMappingManager = vMappingManager;
	}

	public VisualStyleFactory getVisualStyleFactory() {
		return visualStyleFactory;
	}

	public void setVisualStyleFactory(VisualStyleFactory visualStyleFactory) {
		this.visualStyleFactory = visualStyleFactory;
	}

	public CySwingApplication getCytoscapeDesktopService() {
		return cytoscapeDesktopService;
	}

	public void setCytoscapeDesktopService(
			CySwingApplication cytoscapeDesktopService) {
		this.cytoscapeDesktopService = cytoscapeDesktopService;
	}

	public CyNetworkFactory getNetworkFactory() {
		return networkFactory;
	}

	public void setNetworkFactory(CyNetworkFactory networkFactory) {
		this.networkFactory = networkFactory;
	}

	public DataSet getCurrentDataSet(){
		return dataSetMap.get(currentDS);
	}
	
	public String getCurrentDataSetName(){
		return currentDS;
	}
	
	public CyNetworkManager getNetMgr() {
		return netMgr;
	}

	public void setNetMgr(CyNetworkManager netMgr) {
		this.netMgr = netMgr;
	}

	public CyNetwork getCurrentNetwork(){
		return cyApplicationManager.getCurrentNetwork();
	}
	
	public BundleContext getContext() {
		return context;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}

	public DialogTaskManager getDialogTaskManager() {
		return dialogTaskManager;
	}

	public void setDialogTaskManager(DialogTaskManager dialogTaskManager) {
		this.dialogTaskManager = dialogTaskManager;
	}

	public CyApplicationManager getCyApplicationManager() {
		return cyApplicationManager;
	}

	public void setCyApplicationManager(CyApplicationManager cyApplicationManager) {
		this.cyApplicationManager = cyApplicationManager;
	}

	public void loadDataSet(String dataSetId) throws CorruptIndexException, IOException{
		if(dataSetId.equals("")){
			currentDS = "";
			return;
		}
		
		dataSetMap.put(dataSetId, Repository.getInstance().getDataSet(dataSetId));
		currentDS = dataSetId;
	}

	public PathwayItem[] getResultPathways() {
		return resultPathways;
	}

	public void setResultPathways(PathwayItem[] resultPathways) {
		this.resultPathways = resultPathways;
	}
	
	public void setPathway(Pathway pathway){
		pathways.put(pathway.getTitle()+" - "+pathway.getName(), pathway);
	}

	public Pathway getPathway(String id){
		return pathways.get(id);
	}
	
	public String[] getPathways(){
		String[] path = new String[pathways.keySet().size()];
		
		int i = 0;
		for(String key : pathways.keySet()){
			path[i] = key;
			i++;
		}
		Arrays.sort(path);
		return path;
	}
}
