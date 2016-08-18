package de.mpg.biochem.cytokegg.internal;

import java.util.Set;

public class Pathway {

	private String id;
	private String name;
	private String description;
	private Set<String> genes;
	private Set<String> genesInNetwork;
	
	public Pathway(){}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<String> getGenes() {
		return genes;
	}
	
	public void setGenes(Set<String> genes) {
		this.genes = genes;
	}

	public Set<String> getGenesInNetwork() {
		return genesInNetwork;
	}

	public void setGenesInNetwork(Set<String> genesInNetwork) {
		this.genesInNetwork = genesInNetwork;
	}
}
