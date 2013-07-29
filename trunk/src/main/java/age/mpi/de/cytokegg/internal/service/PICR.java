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
