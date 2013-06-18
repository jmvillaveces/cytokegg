package age.mpi.de.cytokegg.internal.ui;

import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cytoscape.work.TaskIterator;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.task.IndexBuilderTask;
import age.mpi.de.cytokegg.internal.util.PluginProperties;


public class UIManager {

	private static UIManager instance = new UIManager();
	private JFrame reference;
	private JDialog repositoryDialog, browsePathDialog, dataSetDialog, pathwaySelectionDialog, CURRENT;
	
	/**
	 * Constructor, private because of singleton
	 */
	private UIManager() {
		reference = CKController.getInstance().getCytoscapeDesktopService().getJFrame();
		
		try{
			repositoryDialog = new RepositoryDialog(reference);
			browsePathDialog = new BrowsePathwayDialog(reference);
			dataSetDialog = new DataSetDialog(reference);
			pathwaySelectionDialog = new PathwaySelectionDialog(reference);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the current instance
	 * @return UIManager
	 */
	public static UIManager getInstance() {
		return instance;
	}
	
	public void find(){
		try{
			checkRepository("dataset");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void browse(){
		try{
		checkRepository("browse");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void repository(){
		try{
			checkRepository("repository");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void update(){
		try{
			if(CURRENT instanceof Updatable){
				((Updatable) CURRENT).update();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void checkRepository(final String action) throws IOException{
    	if(!Repository.getInstance().exists()){
    		
    		int answer = JOptionPane.showConfirmDialog(reference, "It seems like the pathway index has not been created yet.\n"
    				+"Do you want to create it now?");
    		if(answer == JOptionPane.YES_OPTION){
    			IndexBuilderTask task = new IndexBuilderTask(PluginProperties.getInstance().getDefaultOrganisms());
    			CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(task));
    		}
    	}else{
			if(action.equals("repository")){
				setVisibleDialog(DialogEnum.REPOSITORY);
			}else if(action.equals("browse")){
				setVisibleDialog(DialogEnum.BROWSEPATHWAYDIALOG);
			}else if(action.equals("dataset")){
				setVisibleDialog(DialogEnum.DATASET);
			}
		}
    }
	
	public void setVisibleDialog(DialogEnum dialog) {
		if(CURRENT != null){
			CURRENT.setVisible(false);
			CURRENT = null;
		}
		
		CURRENT = getDialog(dialog);
		
		if(CURRENT != null)
			CURRENT.setVisible(true);	
	}
	
	private JDialog getDialog(DialogEnum dialog){
		if(dialog.equals(DialogEnum.REPOSITORY)){
			return repositoryDialog;
		}else if(dialog.equals(DialogEnum.DATASET)){
			return dataSetDialog;
		}else if(dialog.equals(DialogEnum.BROWSEPATHWAYDIALOG)){
			return browsePathDialog;
		}else if(dialog.equals(DialogEnum.PATHWAYSELECTION)){
			return pathwaySelectionDialog;
		}
		return null;
	}
	
	public JFrame getReference(){
		return reference;
	}
}
