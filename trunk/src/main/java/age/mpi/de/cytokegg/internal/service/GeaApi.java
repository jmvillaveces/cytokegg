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
