/*
 * Copyright (C) 2011-2012 Jos� Mar�a Villaveces Max Planck institute for
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
