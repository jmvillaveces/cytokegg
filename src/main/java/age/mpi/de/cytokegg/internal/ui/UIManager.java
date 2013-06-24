/*
 * Copyright (C) 2011-2012 José María Villaveces Max Planck institute for
 * biology of ageing (MPI-age)
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
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
