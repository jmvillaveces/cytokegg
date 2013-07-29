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
package age.mpi.de.cytokegg.internal.model.KGML;

public enum KEGGEntryType {
	ORTHOLOG("ortholog"), ENZYME("enzyme"), GENE("gene"), GROUP("group"), 
	COMPOUND("compound"), MAP("map");

	private String tag;
	
	private KEGGEntryType(final String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public static KEGGEntryType getType(final String tag) {
		for(KEGGEntryType entry: KEGGEntryType.values()) {
			if(entry.getTag().equals(tag))
				return entry;
		}
		return null;
	}
}
