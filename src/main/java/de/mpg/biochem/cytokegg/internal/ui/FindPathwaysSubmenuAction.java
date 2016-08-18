package de.mpg.biochem.cytokegg.internal.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;

public class FindPathwaysSubmenuAction extends AbstractCyAction {

	public FindPathwaysSubmenuAction(CyApplicationManager cyApplicationManager) {
		super("Find pathways in network", cyApplicationManager, null, null);
		setPreferredMenu("Apps.CytoKegg");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		try {
			PathwaysInNetworkDialog dialog = new PathwaysInNetworkDialog();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Could not handle request", "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
		
	}
}
