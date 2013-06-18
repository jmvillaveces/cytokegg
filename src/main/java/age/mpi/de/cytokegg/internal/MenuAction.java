package age.mpi.de.cytokegg.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.work.TaskIterator;

import age.mpi.de.cytokegg.internal.task.NetworkCreationTask;


/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class MenuAction extends AbstractCyAction {

	
	public MenuAction(CyApplicationManager cyApplicationManager , final String menuTitle) {
		
		super(menuTitle, cyApplicationManager, null, null);
		setPreferredMenu("Apps.CytoKegg");
		
	}

	public void actionPerformed(ActionEvent e) {
		
		
			TaskIterator ti = new TaskIterator(new NetworkCreationTask("/Users/jvillaveces/Downloads/hsa04210.xml", false));
			//TaskIterator ti = new TaskIterator(new NetworkCreationTask(new URL("http://www.kegg.jp/kegg-bin/download?entry=hsa04210&format=kgml")));
			CKController.getInstance().getDialogTaskManager().execute(ti);
		
	}
}
