/**
 * Copyright 2013 José María Villaveces Max Planck institute for biology of
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
package age.mpi.de.cytokegg.internal.util;

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
