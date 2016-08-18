package de.mpg.biochem.cytokegg.internal.util;

public class Item implements Comparable<Item>{

    private String id, description;

    public Item(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String toString(){
        return description;
    }

	@Override
	public int compareTo(Item anotherItem) {
		if (!(anotherItem instanceof Item))
		      throw new ClassCastException("An Item object expected.");
		
		return this.getDescription().compareTo(anotherItem.getDescription());
	}
}
