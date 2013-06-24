/*
 * Copyright (C) 2011-2012 José María Villaveces Max Plank institute for biology
 * of ageing (MPI-age)
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
package age.mpi.de.cytokegg.internal.util;

import javax.swing.ImageIcon;

public class IconLoader {
	
	private static IconLoader instance = new IconLoader();
	private ImageIcon REFRESH_IMG;
	private ImageIcon BACK_IMG;
	private ImageIcon FORWARD_IMG;
	private ImageIcon FOLDER_ADD_IMG;
	private ImageIcon DATABASE_ADD_IMG;
	private ImageIcon DATABASE_DELETE_IMG;
	private ImageIcon DATABASE_REFRESH_IMG;
	private ImageIcon MAGNIFIER_IMG;
	private ImageIcon PLAY_IMG;
	private ImageIcon STOP_IMG;
	private ImageIcon TIME_IMG;
	private ImageIcon CYTOKEGG_IMG;
	private ImageIcon CYTOKEGG_NO_EXP_IMG;
	private ImageIcon REPOSITORY;

	/**
	 * Constructor, private because of singleton
	 */
	private IconLoader() {
	}

	/**
	 * Get the current instance
	 * @return IconLoader
	 */
	public static IconLoader getInstance() {
		return instance;
	}
	
	public ImageIcon getRefreshIcon(){
		if(REFRESH_IMG == null)
			REFRESH_IMG = new ImageIcon(getClass().getResource("/icons/arrow_refresh.png"));
		return REFRESH_IMG;
	}
	
	public ImageIcon getBackIcon(){
		if(BACK_IMG == null)
			BACK_IMG = new ImageIcon(getClass().getResource("/icons/arrow_left.png"));
		return BACK_IMG;
	}
	
	public ImageIcon getForwardIcon(){
		if(FORWARD_IMG == null)
			FORWARD_IMG = new ImageIcon(getClass().getResource("/icons/arrow_right.png"));
		return FORWARD_IMG;
	}
	
	public ImageIcon getFolderIcon(){
		if(FOLDER_ADD_IMG == null)
			FOLDER_ADD_IMG = new ImageIcon(getClass().getResource("/icons/folder_add.png"));
		return FOLDER_ADD_IMG;
	}
	
	public ImageIcon getDatabaseAddIcon(){
		if(DATABASE_ADD_IMG == null)
			DATABASE_ADD_IMG = new ImageIcon(getClass().getResource("/icons/database_add.png"));
		return DATABASE_ADD_IMG;
	}
	
	public ImageIcon getDatabaseDeleteIcon(){
		if(DATABASE_DELETE_IMG == null)
			DATABASE_DELETE_IMG = new ImageIcon(getClass().getResource("/icons/database_delete.png"));
		return DATABASE_DELETE_IMG;
	}
	
	public ImageIcon getDatabaseRefreshIcon(){
		if(DATABASE_REFRESH_IMG == null)
			DATABASE_REFRESH_IMG = new ImageIcon(getClass().getResource("/icons/database_refresh.png"));
		return DATABASE_REFRESH_IMG;
	}
	
	public ImageIcon getMagnifierIcon(){
		if(MAGNIFIER_IMG == null)
			MAGNIFIER_IMG = new ImageIcon(getClass().getResource("/icons/magnifier.png"));
		return MAGNIFIER_IMG;
	}
	
	public ImageIcon getPlayIcon(){
		if(PLAY_IMG == null)
			PLAY_IMG = new ImageIcon(getClass().getResource("/icons/control_play.png"));
		return PLAY_IMG;
	}
	
	public ImageIcon getStopIcon(){
		if(STOP_IMG == null)
			STOP_IMG = new ImageIcon(getClass().getResource("/icons/control_stop.png"));
		return STOP_IMG;
	}
	
	public ImageIcon getTimeIcon(){
		if(TIME_IMG == null)
			TIME_IMG = new ImageIcon(getClass().getResource("/icons/time.png"));
		return TIME_IMG;
	}
	
	public ImageIcon getCytoKeggIcon(){
		if(CYTOKEGG_IMG == null)
			CYTOKEGG_IMG = new ImageIcon(getClass().getResource("/icons/cytokeggicon_exp.png"));
		return CYTOKEGG_IMG;
	}
	
	public ImageIcon getCytoKeggNoExpIcon(){
		if(CYTOKEGG_NO_EXP_IMG == null)
			CYTOKEGG_NO_EXP_IMG = new ImageIcon(getClass().getResource("/icons/cytokeggicon.png"));
		return CYTOKEGG_NO_EXP_IMG;
	}
	
	public ImageIcon getRepositoryIcon(){
		if(REPOSITORY == null)
			REPOSITORY = new ImageIcon(getClass().getResource("/icons/repository.png"));
		return REPOSITORY;
	}
	
}
