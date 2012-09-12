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
package org.cytoscape.cytokegg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cytoscape.cytokegg.util.Item;
import org.cytoscape.cytokegg.util.PluginProperties;
import org.cytoscape.cytokegg.util.PathwayItem;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

public class Repository {
	
	private static Repository instance = new Repository();
	
	private String path;
	private FSDirectory dir;

	/**
	 * Constructor, private because of singleton
	 */
	private Repository() {
		path = PluginProperties.getInstance().getIndexPath();
		try {
			dir = FSDirectory.open(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the current instance
	 * @return Repository
	 */
	public static Repository getInstance() {
		return instance;
	}
	
	/**
	 * Returns true if the repository exists
	 * @return
	 * @throws IOException
	 */
	public boolean exists() throws IOException{
		return IndexReader.indexExists(dir);
	}
	
	/**
	 * Deletes the index
	 * @throws IOException
	 */
	public void deleteIndex() throws IOException{
		IndexWriter indexWriter = new IndexWriter(dir ,new StandardAnalyzer(Version.LUCENE_30), !Repository.getInstance().exists(), IndexWriter.MaxFieldLength.UNLIMITED);
		indexWriter.deleteAll();
		indexWriter.close();
	}
	
	/**
	 * Returns true if the organism is indexed, false otherwise
	 * @param organismId
	 * @return boolean
	 * @throws ParseException
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public boolean isOrganismIndexed(String organismId) throws CorruptIndexException, IOException{
        
		Query term1 = new TermQuery(new Term(RepositoryFields.TYPE.getTag(), RepositoryFields.ORGANISM.getTag()));
		Query term2 = new TermQuery(new Term(RepositoryFields.ID.getTag(), organismId));
		
		BooleanQuery query = new BooleanQuery();
		query.add(term1, BooleanClause.Occur.MUST);
		query.add(term2, BooleanClause.Occur.MUST);
		
		if(search(query).size() > 0){
        	return true;
        }
        return false;
	}
	
	/**
	 * Deletes the specified organism
	 * @param orgId
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void deleteOrg(String orgId) throws CorruptIndexException, IOException{
		Term organism = new Term (RepositoryFields.ORGANISM.getTag(), orgId);
		
		IndexWriter indexWriter = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), !Repository.getInstance().exists(), IndexWriter.MaxFieldLength.UNLIMITED);
		indexWriter.deleteDocuments(organism);
		indexWriter.close();
	}
	
	public DataSet getDataSet(String dataSetName) throws CorruptIndexException, IOException{
		
		Query term1 = new TermQuery(new Term(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag()));
		Query term2 = new TermQuery(new Term(RepositoryFields.TITLE.getTag(), dataSetName));
		
		BooleanQuery query = new BooleanQuery();
		query.add(term1, BooleanClause.Occur.MUST);
		query.add(term2, BooleanClause.Occur.MUST);
		
		IndexSearcherWrapper wrapper = search(query);
		if(wrapper.size() > 0){
			return new DataSet(wrapper.get(0));
        }
        return null;	
	}
	
	public String[] getGenesByPathway(String pathwayId) throws CorruptIndexException, IOException{
		Query query = new TermQuery(new Term(RepositoryFields.ID.getTag(), pathwayId));
		
		List<String> genes = new ArrayList<String>();
		IndexSearcherWrapper wrapper = search(query);
		if(wrapper.size() > 0){
        	Document doc = wrapper.get(0);
        	String[] vals = doc.getValues(RepositoryFields.GENE.getTag());
        	for(String s : vals){
        		if(!genes.contains(s)){
        			genes.add(s);
        		}
        	}	
        }
		
		int i = 0;
		String[] g = new String[genes.size()];
		for(String gene : genes){
			g[i] = gene;
			i++;
		}
		Arrays.sort(g);
		return g;
	}
	
	public void deleteDataset(String dataSetName) throws CorruptIndexException, IOException{
		Query term1 = new TermQuery(new Term(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag()));
		Query term2 = new TermQuery(new Term(RepositoryFields.TITLE.getTag(), dataSetName));
		
		BooleanQuery query = new BooleanQuery();
		query.add(term1, BooleanClause.Occur.MUST);
		query.add(term2, BooleanClause.Occur.MUST);
		
		IndexWriter indexWriter = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), !Repository.getInstance().exists(), IndexWriter.MaxFieldLength.UNLIMITED);
		indexWriter.deleteDocuments(query);
		indexWriter.close();
	}
	
	public boolean isDataSetIndexed(String dataSetName) throws CorruptIndexException, IOException{
		Query term1 = new TermQuery(new Term(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag()));
		Query term2 = new TermQuery(new Term(RepositoryFields.TITLE.getTag(), dataSetName));
		
		BooleanQuery query = new BooleanQuery();
		query.add(term1, BooleanClause.Occur.MUST);
		query.add(term2, BooleanClause.Occur.MUST);
		
		if(search(query).size() > 0){
        	return true;
        }
        return false;
	}
	
	public Item[] getIndexedDataSets() throws ParseException, CorruptIndexException, IOException{
	    Query query = new TermQuery(new Term(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag()));
		return  createItemArray(search(query), RepositoryFields.TITLE.getTag(), RepositoryFields.TITLE.getTag(), true);
	}
	
	public String getUniprotId(String geneId) throws CorruptIndexException, IOException{
		Query query = new TermQuery(new Term(RepositoryFields.ALT_ID.getTag(), geneId));
		
		IndexSearcherWrapper wrapper = search(query);
		if(wrapper.size()>0){
			Document doc = wrapper.get(0);
			String[] vals = doc.getValues(RepositoryFields.ALT_ID.getTag());
			for(String val : vals){
				if(val.startsWith("up:")){
					return val.substring(3, val.length());
				}
			}
		}
		
		return "";
	}
	
	public Item[] getIndexedOrganisms() throws ParseException, CorruptIndexException, IOException{
	    Query query = new TermQuery(new Term(RepositoryFields.TYPE.getTag(), RepositoryFields.ORGANISM.getTag()));
		return  createItemArray(search(query), RepositoryFields.ORGANISM.getTag(), RepositoryFields.ORGANISM_DESC.getTag(), true);
	}
	
	public Item[] getPathwaysByOrganism(String org) throws CorruptIndexException, IOException{
		Query query = new TermQuery(new Term(RepositoryFields.ORGANISM_ID.getTag(), org));
		return createItemArray(search(query),RepositoryFields.ID.getTag(),RepositoryFields.TITLE.getTag(), true);
	}
	
	public Item[] getPathwaysByOrganismAndText(String org, String text) throws CorruptIndexException, IOException{
		Query query0 = new TermQuery(new Term(RepositoryFields.ORGANISM_ID.getTag(), org));
		Query query1 = new WildcardQuery(new Term(RepositoryFields.TITLE.getTag(), text));
		
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query0, BooleanClause.Occur.MUST);
		booleanQuery.add(query1, BooleanClause.Occur.MUST);
		
		return createItemArray(search(booleanQuery),RepositoryFields.ID.getTag(),RepositoryFields.TITLE.getTag(), true);
	}
	
	public boolean isGeneInPathway(String path, String gene) throws CorruptIndexException, IOException, ParseException{
		Query query0 = new TermQuery(new Term(RepositoryFields.ID.getTag(), path));
		Query query1 = new TermQuery(new Term(RepositoryFields.GENE.getTag(), gene));
		
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query0, BooleanClause.Occur.MUST);
		booleanQuery.add(query1, BooleanClause.Occur.MUST);
		
		IndexSearcherWrapper wrapper = search(booleanQuery);
		if(wrapper.size()>0){
			return true;
		}
		return false;
	}
	
	public Item[] mapIds(String[] ids) throws CorruptIndexException, IOException{
		
		Item[] items = new Item[ids.length];
		for(int i=0; i< ids.length; i++){
			items[i] = new Item(ids[i], getKeggId(ids[i]));
		}
		return items;
	}
	
	public String getKeggId(String altId) throws CorruptIndexException, IOException{
		Query query = new TermQuery(new Term(RepositoryFields.ALT_ID.getTag(), altId));
		
		IndexSearcherWrapper wrapper = search(query);
		
		String keggId = "";
		if(wrapper.size()>0){
			keggId = wrapper.get(0).get(RepositoryFields.ID.getTag());
		}
		return keggId;
	}
	
	public PathwayItem[] getPathwaysByGenes(List<String> genes) throws CorruptIndexException, IOException, ParseException{
		
		String query = "";
		for(int i=0; i<genes.size(); i++){
			query += "("+genes.get(i)+")";
			if(i != genes.size() -1)
				query += " OR ";
		}
		
		IndexSearcherWrapper wrapper = search(getQuery(RepositoryFields.GENE.getTag(), query, true, Operator.OR));
		
		List<PathwayItem> pItems = new ArrayList<PathwayItem>();
		for(int i=0; i<wrapper.size(); i++) {
			Document pathway = wrapper.get(i);
			String id = pathway.get(RepositoryFields.ID.getTag());
			
			PathwayItem item = new PathwayItem(id, pathway.get(RepositoryFields.TITLE.getTag()), genes.size(), 0);
			pItems.add(item);
			
			for(String gene : genes){
				if(isGeneInPathway(id, gene)){
					item.setInPathway(item.getInPathway()+1);
				}
			}
        }
		
		PathwayItem[] items = new PathwayItem[pItems.size()];
		for(int i=0; i<items.length; i++){
			items[i] = pItems.get(i);
		}
		Arrays.sort(items);
		return items;
	}
	
	private Item[] createItemArray(IndexSearcherWrapper sWrapper, String valueOne, String valueTwo, boolean sort) throws CorruptIndexException, IOException{
		
		Item[] items = new Item[sWrapper.size()];
        for(int i=0;i<items.length;++i) {
            Document d = sWrapper.get(i);
            items[i] = new Item(d.get(valueOne), d.get(valueTwo));
        }
        if(sort)
        	Arrays.sort(items);
        
        return items;
	}
	
	private Query getQuery(String field, String queryStr, boolean escape, Operator op) throws ParseException, CorruptIndexException, IOException{
		QueryParser qp = new QueryParser(Version.LUCENE_30, field, new StandardAnalyzer(Version.LUCENE_30));
		
		if(op != null)
			qp.setDefaultOperator(op);
		
		Query query = (escape) ? qp.parse(QueryParser.escape(queryStr)) : qp.parse(queryStr);
		return query;
	}
	
	private IndexSearcherWrapper search(Query query) throws CorruptIndexException, IOException{
		
		CyLogger.getLogger().info("Lucene Query: "+query.toString());
		
		IndexSearcher searcher = new IndexSearcher(dir);
        TopDocs hits = searcher.search(query, 1);
        
        if(hits.totalHits>1){
        	hits = searcher.search(query, hits.totalHits);
        }
        return new IndexSearcherWrapper(hits, searcher);
	}
	
	public String escape(String str){
		String escapeChars ="[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
		return str.replaceAll(escapeChars, "\\\\$0");
	}	
	
	public void printDocument(Document doc){
		
		List<String> arr = new ArrayList<String>();
		List<Fieldable> fields = doc.getFields();
		for(Fieldable f : fields){
			if(!arr.contains(f.name())){
				System.out.println("--- "+f.name()+" ---");
				String[] vals = doc.getValues(f.name());
				for(String val : vals)
					System.out.println(val);
				
				arr.add(f.name());
			}
		}
		System.out.println("");
	}
}

class IndexSearcherWrapper{
	private TopDocs hits;
	private IndexSearcher searcher;
	
	public IndexSearcherWrapper(TopDocs hits, IndexSearcher searcher) {
		this.hits = hits;
		this.searcher = searcher;
	}
	
	public int size(){
		return hits.totalHits;
	}
	
	public Document get(int index) throws CorruptIndexException, IOException{
		return searcher.doc(hits.scoreDocs[index].doc);
	}
}



