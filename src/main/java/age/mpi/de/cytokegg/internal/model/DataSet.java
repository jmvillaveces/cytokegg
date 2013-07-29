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
package age.mpi.de.cytokegg.internal.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.document.Document;

import age.mpi.de.cytokegg.internal.repository.RepositoryFields;

public class DataSet {

	private String title;
	private Map<String, Double[]> genesExpMap;
	private String[] genes;
	private double min, max;
	private String[] conditions;
	
	public DataSet(Document doc){
		genesExpMap = new HashMap<String, Double[]>();
		
		title = doc.getValues(RepositoryFields.TITLE.getTag())[0];
		min = Double.parseDouble(doc.getValues(RepositoryFields.MIN.getTag())[0]);
		max = Double.parseDouble(doc.getValues(RepositoryFields.MAX.getTag())[0]);
		
		conditions = doc.getValues(RepositoryFields.CONDITION.getTag());
		
		genes = doc.getValues(RepositoryFields.GENE.getTag());
		String[] expression = doc.getValues(RepositoryFields.EXPRESSION.getTag());
		
		Iterator<String> it = Arrays.asList(expression).iterator();
		for(String gene : genes){
			Double[] expArr = new Double[conditions.length];
			for(int i = 0; i<expArr.length; i++){
				expArr[i] = Double.parseDouble(it.next());
			}
			genesExpMap.put(gene, expArr);
		}
	}
	
	public String[] getGenes(){
		return genes;
	}
	
	public String getTitle(){
		return title;
	}
	
	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}
	
	public String[] getConditions(){
		return conditions;
	}

	public int size() {
		return genes.length;
	}
	
	public Double[] getExpression(String gene){
		return genesExpMap.get(gene);
	}
	
	public String toString(){
		String dataset = "ID \t";
		for(String condition : conditions){
			dataset += condition +"\t"; 
		}
		dataset += "\n";
		
		for(String key : genesExpMap.keySet()){
			dataset += key +"\t";
			Double[] exp = genesExpMap.get(key);
			for(double e : exp)
				dataset += e+"\t";
			dataset += "\n";
		}
		
		return dataset;
	}
}
