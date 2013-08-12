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
import org.jdom2.input.SAXBuilder;

import age.mpi.de.cytokegg.internal.util.ParameterNameValue;

public class XMLService {
	
    public XMLService(){}

    /**
     * Retrieves the xml document in the specified url
     * @param url
     * @return
     * @throws org.jdom.JDOMException
     * @throws java.io.IOException
     */
    protected Document getDocument(String url) throws JDOMException, IOException {
        SAXBuilder parser = new SAXBuilder();
        return parser.build(url);
    }

    /**
     * Build an appropiate url for the params array
     * @param url
     * @param params
     * @return
     */
    protected String buildURL(String url, ParameterNameValue[] params){
        StringBuilder locationBuilder = new StringBuilder(url + "?");
        for (int i = 0; i < params.length; i++){
            if (i > 0)
                locationBuilder.append('&');
            locationBuilder.append(params[i].getName()).append('=').append(params[i].getValue());
        }
        return locationBuilder.toString();
    }
}
