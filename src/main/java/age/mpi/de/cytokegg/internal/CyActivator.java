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

import java.util.Properties;

import javax.swing.JPanel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.read.LoadVizmapFileTaskFactory;
import org.cytoscape.task.visualize.ApplyVisualStyleTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;

import age.mpi.de.cytokegg.internal.ui.SidePanel;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		
		CySwingApplication cytoscapeDesktopService = getService(context,CySwingApplication.class);
		CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);
		CyNetworkManager netMgr = getService(context, CyNetworkManager.class);
		CyNetworkFactory networkFactory = getService(context, CyNetworkFactory.class);
		CyTableFactory cyTableFactory = getService(context, CyTableFactory.class);
		VisualMappingManager vMappingManager = getService(context, VisualMappingManager.class);
		VisualStyleFactory visualStyleFactory = getService(context, VisualStyleFactory.class);
		
		//context.
		
		
		// Get a CyNetworkViewFactory
		CyNetworkViewFactory networkViewFactory = getService(context, CyNetworkViewFactory.class);
		CyNetworkViewManager networkViewManager = getService(context, CyNetworkViewManager.class);
		
		LoadVizmapFileTaskFactory loadVizmapFileTaskFactory =  getService(context, LoadVizmapFileTaskFactory.class);
		ApplyVisualStyleTaskFactory applyVisualStyleTaskFactory = getService(context, ApplyVisualStyleTaskFactory.class);
		
		DialogTaskManager dialogTaskManager = getService(context, DialogTaskManager.class);
		
		//Init the controller
		CKController.getInstance().setContext(context);
		CKController.getInstance().setCytoscapeDesktopService(cytoscapeDesktopService);
		CKController.getInstance().setDialogTaskManager(dialogTaskManager);
		CKController.getInstance().setCyApplicationManager(cyApplicationManager);
		CKController.getInstance().setNetMgr(netMgr);
		CKController.getInstance().setNetworkFactory(networkFactory);
		CKController.getInstance().setCyTableFactory(cyTableFactory);
		CKController.getInstance().setvMappingManager(vMappingManager);
		CKController.getInstance().setVisualStyleFactory(visualStyleFactory);
		CKController.getInstance().setNetworkViewFactory(networkViewFactory);
		CKController.getInstance().setNetworkViewManager(networkViewManager);
		CKController.getInstance().setLoadVizmapFileTaskFactory(loadVizmapFileTaskFactory);
		CKController.getInstance().setApplyVisualStyleTaskFactory(applyVisualStyleTaskFactory);
		
		//Test Action
		//MenuAction action = new MenuAction(cyApplicationManager, "Hello World App");
		//registerAllServices(context, action, new Properties());
		
		BrowseAction browse = new BrowseAction(cyApplicationManager, "Browse Pathways");
		registerAllServices(context, browse, new Properties());
		
		RepositoryAction repository = new RepositoryAction(cyApplicationManager, "Repository");
		registerAllServices(context, repository, new Properties());
		
		FindAction find = new FindAction(cyApplicationManager, "Find Pathways by Gene(s)");
		registerAllServices(context, find, new Properties());
		
		//Side Panel
		JPanel sidePanel = new SidePanel(); 
		registerService(context, sidePanel, CytoPanelComponent.class, new Properties());
		registerService(context, sidePanel, NetworkAddedListener.class, new Properties());
		registerService(context, sidePanel, NetworkAboutToBeDestroyedListener.class, new Properties());
	}

}
