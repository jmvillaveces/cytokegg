package age.mpi.de.cytokegg.internal.service;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;

import age.mpi.de.cytokegg.internal.util.ParameterNameValue;

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
