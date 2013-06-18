package age.mpi.de.cytokegg.internal.model;

import java.util.HashMap;
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
	
		for(String gene : genes){
			Double[] expArr = new Double[conditions.length];
			for(int i=0; i<expArr.length; i++){
				expArr[i] = Double.parseDouble(expression[i]);
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
}
