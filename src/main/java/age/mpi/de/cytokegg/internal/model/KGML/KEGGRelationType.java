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

public enum KEGGRelationType {
	EC_REL("ECrel"), PP_REL("PPrel"), GE_REL("GErel"), 
	PC_REL("PCrel"), MAPLINK("maplink"), ACTIVATION("activation"),
	INHIBITION("inhibition"),EXPRESSION("expression"),REPRESION("repression"),
	IN_EFFECT("indirect effect"), STATE_CHANGE("state change"), BINDING("binding/association"),
	DISSOCIATION("dissociation"), MISSING_INTERACTION("missing interaction"),
	PHOSPHORYLATION("phosphorylation"), DEPHOSPHORYLATION("dephosphorylation"),	
	GLYCOSYLATION("glycosylation"), UBIQUITINATION("ubiquitination"),
	METHYLATION("methylation");

	private final String tag;
	
	private KEGGRelationType(final String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return this.tag;
	}
}
