package age.mpi.de.cytokegg.internal.service;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;

import age.mpi.de.cytokegg.internal.util.ParameterNameValue;

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
