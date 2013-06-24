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