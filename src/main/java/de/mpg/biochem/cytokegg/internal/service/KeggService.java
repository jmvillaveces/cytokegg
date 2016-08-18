package de.mpg.biochem.cytokegg.internal.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.http.HTTPException;

import de.mpg.biochem.cytokegg.internal.Pathway;
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
	
	public List<Item> getOrganismIds(String org, String target) throws IOException{
		
		String[] arguments = new String[]{CONV, org, target};
		String url = addArguments(baseUrl, arguments);
		
		return request(url);
	}
	
	public List<Item> getPathwaysByOrg(String org) throws IOException{
		
		if(table.containsKey(org)) return table.get(org);
		
		String[] arguments = new String[]{LIST, "pathway", org};
		String url = addArguments(baseUrl, arguments);
		
		List<Item> paths = request(url);
		table.put(org, paths);
		
		return paths;
	}
	
	public Pathway getPathway(String pathwayId) throws IOException{
		String[] arguments = new String[]{ GET, pathwayId };
		String url = addArguments(baseUrl, arguments);
		
		HttpURLConnection conn = openConnection(url);
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		Pathway path = new Pathway();
		path.setId(pathwayId);
		
		Set<String> genes = new HashSet<String>();
		
		String line = "";
		boolean insideGenes = false;
		while ((line = br.readLine()) != null){
			
			if(line.trim().length() == 0) break;
			
			if(line.startsWith("NAME")){
				line = line.substring(4).trim();
				path.setName(line);
			}else if(line.startsWith("DESCRIPTION")){
				line = line.substring(11).trim();
				path.setDescription(line);
			}else if(line.startsWith("GENE")){
				insideGenes = true;
				line = line.substring(4).trim();
				
				genes.add(line.split(" ")[0]);
				
			}else if(insideGenes){
				if(line.startsWith(" ")){
					line = line.trim();
					genes.add(line.split(" ")[0]);
				}else{
					insideGenes = false;
				}
			}
        }
		br.close();
		closeConnection(conn);
		
		path.setGenes(genes);
		return path;
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
