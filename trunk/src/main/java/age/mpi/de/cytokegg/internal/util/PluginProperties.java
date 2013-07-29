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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PluginProperties {

    private static PluginProperties instance = new PluginProperties();
    private Properties props;

    private PluginProperties(){
        try {
            props = new Properties();
            InputStream inputStream = PluginProperties.class.getResourceAsStream("/plugin.properties");
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
    	return System.getProperty("user.home")+props.getProperty("index");
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
