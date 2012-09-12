package org.cytoscape.cytokegg.data;

import java.io.IOException;

import org.cytoscape.cytokegg.util.ParameterNameValue;
import org.jdom.Document;
import org.jdom.JDOMException;

public class GeaApi extends XMLService {
	
	private final String BASE_URL = "http://www.ebi.ac.uk/gxa/api/vx";
	private Document document;
	
	public GeaApi(){
		super();
	}
	
	public Document getSummary(String accession) throws JDOMException, IOException{
		accession = accession.replace("up:", "");
		ParameterNameValue[] params = new ParameterNameValue[2];
		params[0] = new ParameterNameValue("geneIs", accession);
		params[1] = new ParameterNameValue("format", "xml");
		
		document = getDocument(buildURL(BASE_URL, params));
		return document;
	}
	
	public String getEnsemblGeneId(){
		String ensembl = "";
		
		try{
			ensembl = document.getRootElement().getChild("results").getChild("result").getChild("gene").getChildText("ensemblGeneId");
		}catch(Exception e){
			e.printStackTrace();
		}
		return ensembl;
	}
}
