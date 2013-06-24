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
