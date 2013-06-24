/*
 * Copyright (C) 2011-2012 Jos� Mar�a Villaveces Max Planck institute for
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
package age.mpi.de.cytokegg.internal.model;

import org.cytoscape.model.CyNetwork;


public class Pathway {

	private String name, title, dataSet;
	//private CSPathwayMapper mapper;
	private CyNetwork network;
	
	public Pathway(String name, String title, String dataSet, /*CSPathwayMapper mapper,*/ CyNetwork network) {
		this.name = name;
		this.title = title;
		//this.mapper = mapper;
		this.network = network;
		this.dataSet = dataSet;
	}

	public String getDataSet() {
		return dataSet;
	}

	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	/*public CSPathwayMapper getMapper() {
		return mapper;
	}
	
	public void setMapper(CSPathwayMapper mapper) {
		this.mapper = mapper;
	}*/
	
	public CyNetwork getNetwork() {
		return network;
	}
	
	public void setNetwork(CyNetwork network) {
		this.network = network;
	}
}
