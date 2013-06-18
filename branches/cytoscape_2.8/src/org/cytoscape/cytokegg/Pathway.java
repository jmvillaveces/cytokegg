package org.cytoscape.cytokegg;

import cytoscape.CyNetwork;

public class Pathway {

	private String name, title, dataSet;
	private CSPathwayMapper mapper;
	private CyNetwork network;
	
	public Pathway(String name, String title, String dataSet, CSPathwayMapper mapper, CyNetwork network) {
		this.name = name;
		this.title = title;
		this.mapper = mapper;
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
	
	public CSPathwayMapper getMapper() {
		return mapper;
	}
	
	public void setMapper(CSPathwayMapper mapper) {
		this.mapper = mapper;
	}
	
	public CyNetwork getNetwork() {
		return network;
	}
	
	public void setNetwork(CyNetwork network) {
		this.network = network;
	}
}
