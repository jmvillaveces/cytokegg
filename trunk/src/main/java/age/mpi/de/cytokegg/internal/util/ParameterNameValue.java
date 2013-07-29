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
package age.mpi.de.cytokegg.internal.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ParameterNameValue{

    private final String	name;
    private final String	value;
        
    public ParameterNameValue(String name, String value) throws UnsupportedEncodingException {
        this.name = URLEncoder.encode(name, "UTF-8");
        this.value = URLEncoder.encode(value, "UTF-8");
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}