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
package age.mpi.de.cytokegg.internal.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

import age.mpi.de.cytokegg.internal.util.ParameterNameValue;


public class PICR extends XMLService {

    private final String BASE_URL = "http://www.ebi.ac.uk/Tools/picr/rest/";
    private final String LOG_REF = "logicalCrossReferences";
    private final String IDENT_REF = "identicalCrossReferences";
    private final String UPI_ACC = "getUPIForAccessionReturn";


    public PICR(){
        super();
    }

    public Map<String,List<PicrAccession>> getUPIForAccession(String accession, List<String> databases) throws IOException, JDOMException {
        ParameterNameValue[] params = new ParameterNameValue[databases.size()+1];
        params[0] = new ParameterNameValue("accession", accession);

        for(int i=0; i<databases.size(); i++){
            params[i+1] = new ParameterNameValue("database", databases.get(i));
        }

        Document doc = getDocument(buildURL(BASE_URL+"getUPIForAccession", params));
        List<Element> ch = doc.getRootElement().getChildren();
        Map<String, List<PicrAccession>> accessions = new HashMap<String, List<PicrAccession>>();
        for(Element el : ch){
            if(el.getName().equalsIgnoreCase(UPI_ACC)){
                List<Element> children = el.getChildren();

                for(Element e : children){
                    String tagName = e.getName();
                    if(tagName.equalsIgnoreCase(LOG_REF) || tagName.equalsIgnoreCase(IDENT_REF)){
                        List<Element> eChildren = e.getChildren();
                        String database = "";
                        String acc = "";
                        String dbDesc = "";
                        for(Element ec : eChildren){
                            if(ec.getName().equalsIgnoreCase("accession")){
                                acc = ec.getText();
                            }
                            if(ec.getName().equalsIgnoreCase("databaseName")){
                                database = ec.getText();
                            }
                            if(ec.getName().equalsIgnoreCase("databaseDescription")){
                                dbDesc = ec.getText();
                            }
                        }
                        if(accessions.containsKey(database)){
                            accessions.get(database).add(new PicrAccession(acc,dbDesc,database,tagName));
                        }else{
                            List<PicrAccession> l = new ArrayList<PicrAccession>();
                            l.add(new PicrAccession(acc,dbDesc,database,tagName));
                            accessions.put(database,l);
                        }
                    }
                }
            }
        }
        return accessions;
    }

    public String getFirstIdenticalAccession(String accession, String database) throws IOException, JDOMException {
        ParameterNameValue[] params = new ParameterNameValue[]{
            new ParameterNameValue("accession", accession),
            new ParameterNameValue("database", database)
        };

        Document doc = getDocument(buildURL(BASE_URL + "getUPIForAccession", params));

        Namespace nspace = doc.getRootElement().getNamespace();
        List<Element> ch = doc.getRootElement().getChildren(UPI_ACC,nspace);
        for(Element el : ch){
            List<Element> children = el.getChildren();
            for(Element ele : children){
                if(ele.getName().equalsIgnoreCase(IDENT_REF)){
                    List<Element> eChildren = ele.getChildren();
                    for(Element ec : eChildren){
                        if(ec.getName().equalsIgnoreCase("accession")){
                            return ec.getText();
                        }
                    }
                }
            }
        }
        return "";
    }

}
