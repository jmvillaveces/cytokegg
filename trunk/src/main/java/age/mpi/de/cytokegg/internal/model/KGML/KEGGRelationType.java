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
