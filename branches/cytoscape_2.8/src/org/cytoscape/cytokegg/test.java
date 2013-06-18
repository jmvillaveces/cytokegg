package org.cytoscape.cytokegg;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.cytoscape.cytokegg.kegg.rest.KeggService;
import org.cytoscape.cytokegg.util.Item;

public class test {

	public static void main(String args[]) throws CorruptIndexException, IOException{
		
		Repository.getInstance().getGenesByPathway("path:hsa00514");
		
		/*List<Item> genes = KeggService.getInstance().getGenesByPathway("path:hsa00514");//path:hsa00600 path:hsa00592
		
		String[] genesArr = new String[genes.size()];
		int i = 0;
		for(Item g : genes){
			genesArr[i] = "hsa:"+g.getId();
			i++;
		}
		
		
		String[] targets = new String[]{KeggService.getInstance().NCBI_GENE_ID, KeggService.getInstance().UNIPROT, KeggService.getInstance().NCBI_GI};
		Map<String, List<String>> map = KeggService.getInstance().mapIds(targets, genesArr);
		for(String key : map.keySet()){
			System.out.println("=== "+key+" ===");
			List<String> lst = map.get(key);
			for(String s : lst){
				System.out.println(s);
			}
		}*/
		
	}
}
