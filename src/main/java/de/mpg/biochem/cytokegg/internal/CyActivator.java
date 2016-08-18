package de.mpg.biochem.cytokegg.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;

import de.mpg.biochem.cytokegg.internal.service.KeggService;
import de.mpg.biochem.cytokegg.internal.ui.FindPathwaysSubmenuAction;
import de.mpg.biochem.cytokegg.internal.ui.SearchSubMenuAction;


public class CyActivator extends AbstractCyActivator {
	
	private static DialogTaskManager taskManager;
	private static CySwingApplication cySwingApplication;
	private static LoadVizmapFileTaskFactory loadVizmapFileTaskFactory;
	private static CyNetworkFactory networkFactory;
	private static CyNetworkViewFactory networkViewFactory;
	private static CyNetworkViewManager networkViewManager;
	private static CyNetworkManager networkManager;
	
	public static CyNetworkManager getNetworkManager() {
		return networkManager;
	}


	public CyActivator(){}
	

	@Override
	public void start(BundleContext context) throws Exception {
		
		CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);
		
		Properties properties = new Properties();
		
		SearchSubMenuAction search = new SearchSubMenuAction(cyApplicationManager);
		registerAllServices(context, search, properties);
		
		FindPathwaysSubmenuAction searchPaths = new FindPathwaysSubmenuAction(cyApplicationManager);
		registerAllServices(context, searchPaths, properties);
		
		//Search organisms to make UI fast
		KeggService.getInstance().getOrganisms();
		
		// Load Services
		taskManager = getService(context, DialogTaskManager.class);
		cySwingApplication = getService(context, CySwingApplication.class);
		loadVizmapFileTaskFactory = getService(context, LoadVizmapFileTaskFactory.class);
		
		networkManager = getService(context, CyNetworkManager.class);
		
		networkFactory = getService(context, CyNetworkFactory.class);
		networkViewFactory = getService(context, CyNetworkViewFactory.class);
		networkViewManager = getService(context, CyNetworkViewManager.class);
	}
	
	public static DialogTaskManager getTaskManager(){ 
		return taskManager;
	}
	
	public static CySwingApplication getCySwingApplication(){ 
		return cySwingApplication;
	}
	
	public static LoadVizmapFileTaskFactory getLoadVizmapFileTaskFactory(){ 
		return loadVizmapFileTaskFactory;
	}
	
	public static CyNetworkFactory getNetworkFactory(){ 
		return networkFactory;
	}
	
	public static CyNetworkViewFactory getNetworkViewFactory(){ 
		return networkViewFactory;
	}
	
	public static CyNetworkViewManager getNetworkViewManager(){ 
		return networkViewManager;
	}

}
