package org.cytoscape.cytokegg.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.cytoscape.cytokegg.util.ParameterNameValue;
import org.jdom.Document;
import org.jdom.JDOMException;

public class GEADAS extends XMLService {
	
	private final String BASE_URL = "http://www.ebi.ac.uk/gxa/das/s4/features";
	
	public GEADAS(){
		super();
	}
	
	public Document getSummary(String accession) throws JDOMException, IOException{
		ParameterNameValue[] params = new ParameterNameValue[]{new ParameterNameValue("segment", accession)};
        return getDocument(buildURL(BASE_URL, params));
	}
	
	
	
}
