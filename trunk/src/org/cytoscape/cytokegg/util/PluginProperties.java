/*
 * Copyright (C) 2011-2012 José María Villaveces Max Plank institute for biology
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
package org.cytoscape.cytokegg.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PluginProperties {

    private static PluginProperties instance = new PluginProperties();
    private Properties props;

    private PluginProperties(){
        try {
            props = new Properties();
            InputStream inputStream = getClass().getResourceAsStream("plugin.properties");
            props.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PluginProperties getInstance() {
        return instance;
    }
    
    public String getPluginName(){
    	return props.getProperty("pluginname");
    }
    
    public String getIndexPath(){
    	return props.getProperty("index");
    }
    
    public Item[] getEFactors(){
        return this.getStringMap("efactors");
    }

    public Item[] getEValues(){
        return this.getStringMap("evalues");
    }

    public Item[] getGeneIdFormats(){
        return this.getStringMap("geneidformats");
    }
    
    public int getMaxThreads(){
    	return Integer.parseInt(props.getProperty("maxthreads"));
    }
    
    public Item[] getDefaultOrganisms(){
    	return getStringMap("organisms");
    }
    
    private String[] getStringArray(String property){
    	return props.getProperty(property).split(";");
    }
    
    private Item[] getStringMap(String property){
        String[] factors = props.getProperty(property).split(";");
        Item[] items = new Item[factors.length];

        for(int i=0; i<factors.length; i++){
            String[] factorArr = factors[i].split("\\|");
            items[i] = new Item(factorArr[0], factorArr[1]);
        }
        return items;
    }
}
