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
package age.mpi.de.cytokegg.internal.service;

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

import age.mpi.de.cytokegg.internal.util.Item;

public class KeggService {
	
	private static KeggService instance = new KeggService();
	private String baseUrl = "http://rest.kegg.jp/";
	public final String LIST = "list", GET = "get", INFO = "info", FIND = "find", CONV = "conv", LINK = "link";
	public final String NCBI_GENE_ID = "ncbi-geneid", UNIPROT = "uniprot", NCBI_GI = "ncbi-gi";
	private Item[] organisms;
	
	
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
	
	public List<Item> getGenesByPathway(String path){
		List<Item> genes = new ArrayList<Item>();
		String[] arguments = new String[]{GET, path};
		String url = addArguments(baseUrl, arguments);
		try {
			HttpURLConnection conn = openConnection(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line = "";
			boolean analize = false;
			while ((line = br.readLine()) != null){
				if(line.startsWith("GENE")){
					analize = true;
					line = line.replace("GENE", "");
				}
				
				
				if(!Character.isDigit(line.trim().charAt(0))){
					analize = false;
				}
				
				if(analize){
					String[] geneArr = line.substring(0,line.indexOf(";")).replace("GENE", "").trim().split("  ");
					if(geneArr.length>=2)
						genes.add(new Item(geneArr[0], geneArr[1]));
				}
	        }
			br.close();
			closeConnection(conn);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return genes;
	}
	
	public Map<String, List<String>> mapIds(String[] targets, String[] genes){
		Map<String,List<String>> mapedGenes = new HashMap<String,List<String>>();
		for(String target : targets){
			Map<String, List<String>> map = mapIds(target, genes);
			for(String key : map.keySet()){
				if(mapedGenes.containsKey(key)){
					List<String> mainLst = mapedGenes.get(key);
					List<String> lst = map.get(key);
					for(String s : lst){
						if(!mainLst.contains(s))
							mainLst.add(s);
					}
    			}else{
    				mapedGenes.put(key, map.get(key));
    			}
			}
		}
		return mapedGenes;
	}
	
	public Map<String, List<String>> mapIds(String target, String[] genes){
		Map<String,List<String>> mapedGenes = new HashMap<String,List<String>>();
		
		String query = "";
		for(String gene : genes){
			query += gene + "+";
		}
		//query = query.substring(0, query.lastIndexOf("+"));
		
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
	
	public List<Item> getPathwaysByOrg(String org){
		String[] arguments = new String[]{LIST, "pathway", org};
		String url = addArguments(baseUrl, arguments);
		try {
			List<Item> pathways = new ArrayList<Item>();
			HttpURLConnection conn = openConnection(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line = "";
			while ((line = br.readLine()) != null){
				String[] lineArr = line.split("\t");
				pathways.add(new Item(lineArr[0], lineArr[1]));
	        }
			br.close();
			closeConnection(conn);
			
			return pathways;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<Item>();
	}
	
	public Item[] getOrganisms(){
		
		if(organisms == null){
			String[] arguments = new String[]{LIST, "organism"};
			String url = addArguments(baseUrl, arguments);
			try {
				List<Item> orgs = new ArrayList<Item>();
				HttpURLConnection conn = openConnection(url);
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				
				String line = "";
				while ((line = br.readLine()) != null){
					String[] lineArr = line.split("\t");
					orgs.add(new Item(lineArr[1], lineArr[2]));
		        }
				br.close();
				closeConnection(conn);
				
				int i = 0;
				Item[] orgsArr = new Item[orgs.size()];
				for(Item it : orgs){
					orgsArr[i] = it;
					i++;
				}
				organisms = orgsArr;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return organisms;
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
