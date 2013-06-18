package age.mpi.de.cytokegg.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;

import age.mpi.de.cytokegg.internal.ui.UIManager;

public class BrowseAction extends AbstractCyAction {

	public BrowseAction(CyApplicationManager cyApplicationManager, final String menuTitle) {
		super(menuTitle, cyApplicationManager, null, null);
		setPreferredMenu("Apps.CytoKegg");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		UIManager.getInstance().browse();
	}

}
