/**
 * Copyright 2013 Jos� Mar�a Villaveces Max Planck institute for biology of
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
package age.mpi.de.cytokegg.internal.ui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cytoscape.work.TaskIterator;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.repository.Repository;
import age.mpi.de.cytokegg.internal.task.GEAExpressionTask;
import age.mpi.de.cytokegg.internal.task.IndexBuilderTask;
import age.mpi.de.cytokegg.internal.util.PluginProperties;


public class UIManager {

	private static UIManager instance = new UIManager();
	private JFrame reference;
	private JDialog repositoryDialog, browsePathDialog, dataSetDialog, pathwaySelectionDialog, CURRENT;
	private Map<String, JFrame> expWindows = new HashMap<String, JFrame>();
	
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
	
	private void checkRepository(final String action) throws Exception{
    	if(!Repository.getInstance().exists()){
    		
    		int answer = JOptionPane.showConfirmDialog(reference, "It seems like the pathway index has not been created yet.\n"
    				+"Do you want to create it now?");
    		if(answer == JOptionPane.YES_OPTION){
    			IndexBuilderTask task = new IndexBuilderTask(PluginProperties.getInstance().getDefaultOrganisms());
    			CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(task));
    		}
    	}else{
			if(action.equals("repository")){
				((RepositoryDialog) repositoryDialog).update();
				setVisibleDialog(DialogEnum.REPOSITORY);
			}else if(action.equals("browse")){
				setVisibleDialog(DialogEnum.BROWSEPATHWAYDIALOG);
			}else if(action.equals("dataset")){
				((DataSetDialog) dataSetDialog).update();
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
	
	public void openExpressionWindow(String id) {
		if(expWindows.containsKey(id) && expWindows.get(id) != null){
    		expWindows.get(id).setVisible(true);
    	}else{
	    	GEAExpressionTask task = new GEAExpressionTask(id);
	        CKController.getInstance().getDialogTaskManager().execute(new TaskIterator(task));
    	}
	}
	
	public void addExpressionFrame(String id, JFrame frame){
		expWindows.put(id, frame);
	}
}
