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
package org.cytoscape.cytokegg.util;

public class PathwayItem implements Comparable<PathwayItem>{

	private String id;
	private String title;
	private int total;
	private int inPathway;
	
	public PathwayItem(String id, String title, int total, int inPathway) {
		this.id = id;
		this.title = title;
		this.total = total;
		this.inPathway = inPathway;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getTotal() {
		return total;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getInPathway() {
		return inPathway;
	}
	
	public void setInPathway(int inPathway) {
		this.inPathway = inPathway;
	}

	public String toString(){
       return title +" - "+inPathway+ " of "+total;
    }
	
	@Override
	public int compareTo(PathwayItem anotherPathwayItem) {
		return anotherPathwayItem.getInPathway() - this.getInPathway();
	}
}
