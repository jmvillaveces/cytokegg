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
package org.cytoscape.cytokegg;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.cytoscape.cytokegg.ui.GUIManager;
import org.cytoscape.cytokegg.util.PathwayItem;

public class Plugin {
	
	private static Plugin instance = new Plugin();
	private Map<String, DataSet> dataSetMap;
	private CSPathwayMapper mapper;
	private Map<String, Pathway> pathways;
	private String currentDS;
	private PathwayItem[] resultPathways;

	/**
	 * Constructor, private because of singleton
	 */
	private Plugin() {
		//Init UI
		GUIManager.getInstance();
		dataSetMap = new HashMap<String, DataSet>();
		pathways = new HashMap<String, Pathway>();
		currentDS = "";
	}

	/**
	 * Get the current instance
	 * @return Plugin
	 */
	public static Plugin getInstance() {
		return instance;
	}
	
	public void loadDataSet(String dataSetId) throws CorruptIndexException, IOException{
		if(dataSetId.equals("")){
			currentDS = "";
			return;
		}
		
		dataSetMap.put(dataSetId, Repository.getInstance().getDataSet(dataSetId));
		currentDS = dataSetId;
	}
	
	public DataSet getCurrentDataSet(){
		return dataSetMap.get(currentDS);
	}
	
	public String getCurrentDataSetName(){
		return currentDS;
	}
	
	public DataSet getDataSet(String dataSetId){
		return dataSetMap.get(dataSetId);
	}
	
	public CSPathwayMapper getMapper() {
		return mapper;
	}

	public void setMapper(CSPathwayMapper mapper) {
		this.mapper = mapper;
	}
	
	public void setPathway(Pathway pathway){
		pathways.put(pathway.getTitle()+" - "+pathway.getName(), pathway);
	}

	public Pathway getPathway(String id){
		return pathways.get(id);
	}
	
	public void setResultPathways(PathwayItem[] pathways) {
		resultPathways = pathways;
	}
	
	public PathwayItem[] getResultPathways() {
		return resultPathways;
	}
	
	public String[] getPathways(){
		String[] path = new String[pathways.keySet().size()];
		
		int i = 0;
		for(String key : pathways.keySet()){
			path[i] = key;
			i++;
		}
		Arrays.sort(path);
		return path;
	}
}
