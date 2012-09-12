package org.cytoscape.cytokegg.data.reader.kgml;

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
