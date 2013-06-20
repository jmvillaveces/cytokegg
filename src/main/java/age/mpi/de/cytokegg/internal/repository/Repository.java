package age.mpi.de.cytokegg.internal.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import age.mpi.de.cytokegg.internal.model.DataSet;
import age.mpi.de.cytokegg.internal.util.Item;
import age.mpi.de.cytokegg.internal.util.PathwayItem;
import age.mpi.de.cytokegg.internal.util.PluginProperties;

public class Repository {
	
	private static Repository instance = new Repository();
	
	private Version lVersion = Version.LUCENE_43;
	private IndexWriterConfig writerConfig;
	private String path;
	private FSDirectory dir;
	private IndexSearcher searcher; 
	
	/**
	 * Constructor, private because of singleton
	 */
	private Repository() {
		path = PluginProperties.getInstance().getIndexPath();
		writerConfig = new IndexWriterConfig(lVersion, new StandardAnalyzer(lVersion));
		try {
			dir = FSDirectory.open(new File(path));
			initSearcher();
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
	public boolean exists(){
		try {
			Item[] items = getIndexedOrganisms();
			if(items.length>0){
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deletes the index
	 * @throws IOException
	 */
	public void deleteIndex() throws IOException{
		IndexWriter indexWriter = new IndexWriter(dir, writerConfig);
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
		
		IndexWriter indexWriter = new IndexWriter(dir, writerConfig);
		indexWriter.deleteDocuments(organism);
		indexWriter.commit();
		indexWriter.close();
		
		initSearcher();
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
	
	public void deleteDataset(String dataSetName) throws CorruptIndexException, IOException{
		Query term1 = new TermQuery(new Term(RepositoryFields.TYPE.getTag(), RepositoryFields.DATASET.getTag()));
		Query term2 = new TermQuery(new Term(RepositoryFields.TITLE.getTag(), dataSetName));
		
		BooleanQuery query = new BooleanQuery();
		query.add(term1, BooleanClause.Occur.MUST);
		query.add(term2, BooleanClause.Occur.MUST);
		
		IndexWriter indexWriter = new IndexWriter(dir, writerConfig);
		indexWriter.deleteDocuments(query);
		indexWriter.commit();
		indexWriter.close();
		
		initSearcher();
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
		String queryStr = "+"+RepositoryFields.GENE.getTag()+":"+gene.replace(":", "\\:")+" +"+RepositoryFields.ID.getTag()+":"+path.replace(":", "\\:");
		//+gene:hsa:4357 +id:path:hsa04612
		
		//Query query0 = new QueryParser(lVersion, RepositoryFields.ID.getTag(), new StandardAnalyzer(lVersion)).parse(path.replace(":", "\\:"));
		//Query query1 = new QueryParser(lVersion, RepositoryFields.GENE.getTag(), new StandardAnalyzer(lVersion)).parse(gene.replace(":", "\\:"));
		
		/*Query query1 = new TermQuery(new Term(RepositoryFields.GENE.getTag(), gene.replace(":", "\\:")));
		
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query0, BooleanClause.Occur.MUST);
		booleanQuery.add(query1, BooleanClause.Occur.MUST);
		
		System.out.println(booleanQuery);*/
		
		Query query = new QueryParser(lVersion, RepositoryFields.GENE.getTag(), new StandardAnalyzer(lVersion)).parse(queryStr);
		IndexSearcherWrapper wrapper = search(query);
		if(wrapper.size()>0){
			return true;
		}
		return false;
	}
	
	/*public Item[] mapIds(String[] ids) throws CorruptIndexException, IOException{
		
		Item[] items = new Item[ids.length];
		for(int i=0; i< ids.length; i++){
			items[i] = new Item(ids[i], getKeggId(ids[i]));
		}
		return items;
	}*/
	
	public String getKeggId(String altId) throws CorruptIndexException, IOException, ParseException{
		Query query = new QueryParser(lVersion, RepositoryFields.ALT_ID.getTag(), new StandardAnalyzer(lVersion)).parse(altId);
		IndexSearcherWrapper wrapper = search(query);
		
		String keggId = "";
		if(wrapper.size()>0){
			keggId = wrapper.get(0).get(RepositoryFields.ID.getTag());
		}
		return keggId;
	}

	public PathwayItem[] getPathwaysByGenes(List<String> genes) throws CorruptIndexException, IOException, ParseException{
		
		String queryStr = "";
		for(int i=0; i<genes.size(); i++){
			queryStr += "\""+genes.get(i)+"\"";
			if(i != genes.size() -1)
				queryStr += " OR ";
		}
		Query query =  new QueryParser(lVersion, RepositoryFields.GENE.getTag(), new StandardAnalyzer(lVersion)).parse(queryStr);
	
		IndexSearcherWrapper wrapper = search(query);
		
		List<PathwayItem> pItems = new ArrayList<PathwayItem>();
		for(int i=0; i<wrapper.size(); i++) {
			Document pathway = wrapper.get(i);
			String id = pathway.get(RepositoryFields.ID.getTag());
			
			PathwayItem item = new PathwayItem(id, pathway.get(RepositoryFields.TITLE.getTag()), genes.size(), 0);
			pItems.add(item);
			
			List<String> pathwayGenes = Arrays.asList(pathway.getValues(RepositoryFields.GENE.getTag()));
			for(int j=0; j<genes.size(); j++){
				if(pathwayGenes.contains(genes.get(j)))
					item.setInPathway(item.getInPathway()+1);
			}
		}
		
		PathwayItem[] items = pItems.toArray(new PathwayItem[0]);
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
	
	private IndexSearcherWrapper search(Query query) throws CorruptIndexException, IOException{
        if(searcher == null){
        	searcher = new IndexSearcher(DirectoryReader.open(dir));
        }
		
		TopDocs hits = searcher.search(query, 1);
        
        if(hits.totalHits>1){
        	hits = searcher.search(query, hits.totalHits);
        }
        return new IndexSearcherWrapper(hits, searcher);
	}
	
	public void printDocument(Document doc){
		
		List<String> arr = new ArrayList<String>();
		List<IndexableField> fields = doc.getFields();
		for(IndexableField f : fields){
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
	
	public void initSearcher() throws IOException{
		searcher = new IndexSearcher(DirectoryReader.open(dir));
	}
	
	/**
	 * Force deletion of directory
	 * @param path
	 * @return
	 */
	private boolean deleteDirectory(File path) {
	    if (path.exists()) {
	        File[] files = path.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            if (files[i].isDirectory()) {
	                deleteDirectory(files[i]);
	            } else {
	                files[i].delete();
	            }
	        }
	    }
	    return (path.delete());
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