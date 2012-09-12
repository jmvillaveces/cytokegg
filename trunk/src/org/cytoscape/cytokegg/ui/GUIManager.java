package org.cytoscape.cytokegg.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.cytoscape.cytokegg.Repository;
import org.cytoscape.cytokegg.task.GEAExpressionTask;
import org.cytoscape.cytokegg.task.IndexBuilderTask;
import org.cytoscape.cytokegg.util.PluginProperties;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelImp;

public class GUIManager {
	
	private static GUIManager instance = new GUIManager();
	private Map<String, JFrame> expWindows = new HashMap<String, JFrame>();
	private JDialog CURRENT;
	private JFrame repository;
	private JPanel sidePanel;
	
	/**
     * Constructor, private because it is a singleton
     */
	private GUIManager(){
		
		final JMenu menu = new JMenu(PluginProperties.getInstance().getPluginName());

        Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(menu);
        
        menu.add(new JMenuItem(new AbstractAction("Find Pathways by Gene(s)") {
            public void actionPerformed(ActionEvent e) {
            	try {
					if(checkRepository()){
						setVisibleDialog(Dialogs.DATASET);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            }
        }));

        menu.add(new JMenuItem(new AbstractAction("Browse Pathways") {
			public void actionPerformed(ActionEvent e) {
				try {
					if (checkRepository()) {
						setVisibleDialog(Dialogs.BROWSEPATHWAYDIALOG);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}));
        
        menu.add(new JMenuItem(new AbstractAction("Repository") {
			public void actionPerformed(ActionEvent e) {
				try {
					if (checkRepository()) {
						if(repository == null)
							new RepositoryForm();
						
						repository.setVisible(true);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}));
	}
	
	/**
     * Returns the unique instance of GUIManager
     * @return GUIManager
     */
    public static GUIManager getInstance() {
        return instance;
    }
    
    private boolean checkRepository() throws IOException{
    	if(!Repository.getInstance().exists()){
    		
    		int answer = JOptionPane.showConfirmDialog(null, "It seems like the pathway index has not been created yet.\n"
    				+"Do you want to create it now?");
    		if(answer == JOptionPane.YES_OPTION){
    			Task task = new IndexBuilderTask(PluginProperties.getInstance().getDefaultOrganisms());
                JTaskConfig config = new JTaskConfig();
                config.displayCancelButton(false);
                config.displayStatus(true);
                config.displayTimeElapsed(true);
                
                boolean success = TaskManager.executeTask(task, config);
                if(!success){
                	Repository.getInstance().deleteIndex();
                	JOptionPane.showMessageDialog(null, "Error while indexing the data. The index has been deleted!");
                	return false;
                }
                return true;
    		}else{
    			return false;
    		}
    	}
    	return true;
    }

	public void setVisibleDialog(Dialogs dialog) {
		if(CURRENT != null){
			CURRENT.setVisible(false);
			CURRENT = null;
		}
		
		setCurrentDialog(dialog.getTag());
		
		if(CURRENT != null)
			CURRENT.setVisible(true);	
	}
	
	private void setCurrentDialog(int dialog){
		
		if(dialog == Dialogs.DATASET.getTag()){
			CURRENT = new DataSetDialog(Cytoscape.getDesktop());
		}else if(dialog == Dialogs.FILEIMPORT.getTag()){
			CURRENT = new GeneListImportDialog(Cytoscape.getDesktop());
		}else if(dialog == Dialogs.NETWORKIMPORT.getTag()){
			CURRENT = new GeneListImportDialog(Cytoscape.getDesktop());
		}else if(dialog == Dialogs.PATHWAYSELECTION.getTag()){
			CURRENT = new PathwaySelectionDialog(Cytoscape.getDesktop());
		}else if(dialog == Dialogs.BROWSEPATHWAYDIALOG.getTag()){
			CURRENT = new BrowsePathwayDialog(Cytoscape.getDesktop());
		}
	}

	public void setSidePanel() {
		
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
		if(sidePanel != null){
			cytoPanel.remove(cytoPanel.indexOfComponent(PluginProperties.getInstance().getPluginName()));
		}
		
		sidePanel = new SidePanel();
		sidePanel.repaint();
		sidePanel.updateUI();
		cytoPanel.add(PluginProperties.getInstance().getPluginName(), sidePanel);
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(PluginProperties.getInstance().getPluginName()));
		
	}

	public void openExpressionWindow(String id) {
		if(expWindows.containsKey(id) && expWindows.get(id) != null){
    		expWindows.get(id).setVisible(true);
    	}else{
	    	GEAExpressionTask task = new GEAExpressionTask(id);
	        JTaskConfig config = new JTaskConfig();
	        config.displayCancelButton(true);
	
	        boolean success = TaskManager.executeTask(task, config);
	        if(success && task.getPanel() != null){
	        	GEASummaryPanel panel = task.getPanel();
	        	JFrame frame = new JFrame();
	        	frame.setContentPane(panel);
	        	frame.setSize(new Dimension(750, 500));
				frame.setVisible(true);
				
				expWindows.put(id, frame);
	        }else{
	        	JOptionPane.showMessageDialog(null, "No expression summary for "+id);
	        }
    	}
	}
}
