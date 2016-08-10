package de.mpg.biochem.cytokegg.internal.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

import de.mpg.biochem.cytokegg.internal.util.Item;

public class KeggService {
	
	private static KeggService instance = new KeggService();
	private String baseUrl = "http://rest.kegg.jp/";
	
	private final String PATHWAY = "pathway";
	
	public final String LIST = "list", GET = "get", INFO = "info", FIND = "find", CONV = "conv", LINK = "link";
	public final String NCBI_GENE_ID = "ncbi-geneid", UNIPROT = "uniprot", NCBI_GI = "ncbi-gi";
	
	private Map<String, List<Item>> table = new HashMap<String, List<Item>>();
	
	
	/**
	 * Constructor, private because of singleton
	 */
	private KeggService() {}

	/**
	 * Get the current instance
	 * @return KeggService
	 */
	public static KeggService getInstance() {
		return instance;
	}
	
	public List<Item> getGenesInPathway(String path){
		List<Item> genes = new ArrayList<Item>();
		String[] arguments = new String[]{GET, path};
		String url = addArguments(baseUrl, arguments);
		
		
		return genes;
	}
	
	public List<Item> find(String query) throws IOException{
		
		String[] arguments = new String[]{FIND, PATHWAY, query};
		String url = addArguments(baseUrl, arguments);

		return request(url);
	}
	
	public Map<String, List<String>> mapIds(String[] targets, String[] genes){
		Map<String,List<String>> mappedGenes = new HashMap<String,List<String>>();
		for(String target : targets){
			Map<String, List<String>> map = mapIds(target, genes);
			for(String key : map.keySet()){
				if(mappedGenes.containsKey(key)){
					List<String> mainLst = mappedGenes.get(key);
					List<String> lst = map.get(key);
					for(String s : lst){
						if(!mainLst.contains(s))
							mainLst.add(s);
					}
    			}else{
    				mappedGenes.put(key, map.get(key));
    			}
			}
		}
		return mappedGenes;
	}
	
	public Map<String, List<String>> mapIds(String target, String[] genes){
		Map<String,List<String>> mapedGenes = new HashMap<String,List<String>>();
		
		String query = "";
		for(String gene : genes)
			query += gene + "+";
		
		String[] arguments = new String[]{CONV, target};
		String url = addArguments(baseUrl, arguments) + query;
		
		try {
			HttpURLConnection conn = openConnection(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line = "";
			while ((line = br.readLine()) != null){
				String[] lineArr = line.split("\t");
				
				if(mapedGenes.containsKey(lineArr[0])){
    				mapedGenes.get(lineArr[0]).add(lineArr[1]);
    			}else{
    				List<String> lst = new ArrayList<String>();
    				lst.add(lineArr[1]);
    				mapedGenes.put(lineArr[0], lst);
    			}
	        }
			br.close();
			closeConnection(conn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapedGenes;
	}
	
	public List<Item> getPathwaysByOrg(String org) throws IOException{
		
		if(table.containsKey(org)) return table.get(org);
		
		String[] arguments = new String[]{LIST, "pathway", org};
		String url = addArguments(baseUrl, arguments);
		
		List<Item> paths = request(url);
		table.put(org, paths);
		
		return paths;
	}
	
	public List<Item> getOrganisms() throws IOException{
		
		if(table.containsKey("organism")) return table.get("organism");
		
		String[] arguments = new String[]{LIST, "organism"};
		String url = addArguments(baseUrl, arguments);
		
		List<Item> orgs = request(url, 1, 2);
		table.put("organism", orgs);
		
		return orgs;
	}
	
	private List<Item> request(String url) throws IOException{
		return request(url, 0, 1);
	}
	
	private List<Item> request(String url, int i, int j) throws IOException{
		
		HttpURLConnection conn = openConnection(url);
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		List<Item> items = new ArrayList<Item>();
		
		String line = "";
		while ((line = br.readLine()) != null){
			
			if(line.trim().length() == 0) break;
			
			String[] lineArr = line.split("\t");
			items.add(new Item(lineArr[i], lineArr[j]));
        }
		br.close();
		closeConnection(conn);
		
		return items;
	}
	
	private HttpURLConnection openConnection(String url){
		HttpURLConnection huc = null;
		
		try {
			URL u = new URL(url); 
			huc =  (HttpURLConnection)  u.openConnection();
			huc.setRequestMethod("GET"); 
			huc.connect();
			    
			if(huc.getResponseCode() != 200){
				throw new HTTPException(huc.getResponseCode());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return huc;
	}
	
	private void closeConnection(HttpURLConnection conn){
		conn.disconnect();
		conn = null;
	}
	
	private String addArguments(String url, String[] arguments){
		for(String arg : arguments)
			url = addArgument(url, arg);
		return url;
	}
	
	private String addArgument(String url, String argument){
		return url+argument+"/";
	}
}
