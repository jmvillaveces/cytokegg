/*
 * Copyright (C) 2011-2012 Jos� Mar�a Villaveces Max Plank institute for biology
 * of ageing (MPI-age)
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
