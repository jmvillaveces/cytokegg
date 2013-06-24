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
