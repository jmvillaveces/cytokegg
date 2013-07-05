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
package age.mpi.de.cytokegg.internal.task;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

import age.mpi.de.cytokegg.internal.CKController;
import age.mpi.de.cytokegg.internal.generated.Component;
import age.mpi.de.cytokegg.internal.generated.Entry;
import age.mpi.de.cytokegg.internal.generated.Graphics;
import age.mpi.de.cytokegg.internal.generated.Pathway;
import age.mpi.de.cytokegg.internal.generated.Product;
import age.mpi.de.cytokegg.internal.generated.Reaction;
import age.mpi.de.cytokegg.internal.generated.Relation;
import age.mpi.de.cytokegg.internal.generated.Substrate;
import age.mpi.de.cytokegg.internal.generated.Subtype;

public class NetworkCreationTask extends AbstractTask{
	
	private static final String PACKAGE_NAME = "age.mpi.de.cytokegg.internal.generated";

	/* Network attrs */
	static final String NETWORK_TYPE = "network type";
	static final String NETWORK_TYPE_VALUE = "KEGG Pathway";
	static final String SPECIES = "KEGG.org";
	static final String NUMBER = "KEGG.number";
	static final String IMAGE = "KEGG.image";
	static final String LINK = "KEGG.link";
	static final String TITLE = "KEGG.title";
	
	/* Public attrs */
	private static final String KEGG_NAME = "KEGG.name";
	private static final String KEGG_NAME_LIST = "KEGG.name.list";
	private static final String KEGG_ENTRY = "KEGG.entry";
	private static final String KEGG_LABEL = "KEGG.label";
	private static final String KEGG_LABEL_LIST = "KEGG.label.list";
	private static final String KEGG_LABEL_LIST_FIRST = "KEGG.label.first";
	private static final String KEGG_RELATION = "KEGG.relation";
	private static final String KEGG_REACTION = "KEGG.reaction";
	private static final String KEGG_REACTION_LIST = "KEGG.reaction.list";
	private static final String KEGG_LINK = "KEGG.link";
	private static final String KEGG_TYPE = "KEGG.type";
	private static final String KEGG_COLOR = "KEGG.color";
	private static final String KEGG_WIDTH = "KEGG.width";
	private static final String KEGG_HEIGHT = "KEGG.height";
	private static final String KEGG_X = "KEGG.x";
	private static final String KEGG_Y = "KEGG.y";
	private static final String KEGG_ID = "KEGG.id";
	
	// Special cases: Global Map
	private static final String METABOLIC_PATHWAYS_ENTRY_ID = "01100";
	private static final String BIOSYNTHESIS_OF_SECONDARY_METABOLITES_ENTRY_ID = "01110";

	private static final String REACTION_TYPE_REVERSIBLE = "reversible";
	private static final String REACTION_TYPE_IRREVERSIBLE = "irreversible";

	private CyNetwork pathwayNet;
	private CyRootNetwork rootNet;
	private CyTable nodeTable, edgeTable;

	private Map<Integer, Integer> nodeGroupMap;
	private Map<String, CyNetwork> networkMap;
	
	private URL targetURL;
	private String networkName;
	private Pathway pathway;

	private boolean annotate;
	
	public NetworkCreationTask(final String fileName, boolean annotate) {
		System.out.println("Debug: KGML File name = " + fileName);
		
		this.annotate = annotate;
		try {
			this.targetURL = (new File(fileName)).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public NetworkCreationTask(final URL url, boolean annotate) {
		this.annotate = annotate;
		System.out.println("Debug: KGML URL name = " + url);
		this.targetURL = url;
	}

	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		InputStream is = null;
		pathway = null;

		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE_NAME, this.getClass().getClassLoader());
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			is = targetURL.openStream();
			pathway = (Pathway) unmarshaller.unmarshal(is);
			networkName = networkName + " (" + pathway.getOrg() + ")";
			
			pathwayNet = CKController.getInstance().getNetworkFactory().createNetwork();
			pathwayNet.getRow(pathwayNet).set(CyNetwork.NAME, pathway.getTitle());
			
			rootNet = ((CySubNetwork) pathwayNet).getRootNetwork();
			
			CyTable nTable = pathwayNet.getDefaultNetworkTable();
			nTable.createColumn(SPECIES, String.class, false);
			nTable.createColumn(NUMBER, String.class, false);
			nTable.createColumn(IMAGE, String.class, false);
			nTable.createColumn(LINK, String.class, false);
			nTable.createColumn(NETWORK_TYPE, String.class, false);
			
			CyRow netRow = pathwayNet.getRow(pathwayNet);
			netRow.set(SPECIES, pathway.getOrg());
			netRow.set(NUMBER, pathway.getNumber());
			netRow.set(IMAGE, pathway.getImage());
			netRow.set(LINK, pathway.getLink());
			netRow.set(NETWORK_TYPE, NETWORK_TYPE_VALUE);
			
			createTableFields();
			
			mapEntries(pathway.getEntry());
			
			List<Reaction> reactions = pathway.getReaction();
			for(Reaction reaction : reactions){
				mapReaction(reaction);
			}
			
			List<Relation> relations = pathway.getRelation();
			for(Relation relation : relations){
				mapRelation(relation);
			}
			
			CKController.getInstance().getNetMgr().addNetwork(pathwayNet);
			
			TaskIterator ti;
			if(annotate){
				ti = new TaskIterator(new NetworkExpresionAnnotationTask(rootNet));
				ti.append(new NetworkViewTask(rootNet.getSubNetworkList()));
			}else{
				ti = new TaskIterator(new NetworkViewTask(rootNet.getSubNetworkList()));
			}
			this.insertTasksAfterCurrentTask(ti);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
	
	private void mapEntries(List<Entry> entries){
		Map<String, Entry> entryMap = new HashMap<String, Entry>();
		Map<String, Entry> groupMap = new HashMap<String, Entry>();
		nodeGroupMap = new HashMap<Integer, Integer>();
		networkMap = new HashMap<String, CyNetwork>();
		
		Iterator<Entry> i = entries.iterator();
		while(i.hasNext()){
			Entry entry = i.next();
			if(entry.getType().equals("group")){
				groupMap.put(entry.getId(), entry);
			}else{
				entryMap.put(entry.getId(), entry);
			}
		}
		
		int count =0;
		Iterator<String> j = groupMap.keySet().iterator();
		while(j.hasNext()){
			count ++;
			CyNetwork net = rootNet.addSubNetwork();
			net.getRow(net).set(CyNetwork.NAME, pathway.getTitle()+" - subnetwork "+count);
			
			Entry group = groupMap.get(j.next());
			int groupId = Integer.parseInt(group.getId());
			for(Component c : group.getComponent()){
				mapEntry(entryMap.get(c.getId()), net);
				entryMap.remove(c.getId());
				nodeGroupMap.put(Integer.parseInt(c.getId()), groupId);
			}
			
			CKController.getInstance().getNetMgr().addNetwork(net);
			mapEntry(group, pathwayNet).setNetworkPointer(net);
			networkMap.put(group.getId(), net);
		}
		
		j = entryMap.keySet().iterator();
		while(j.hasNext()){
			mapEntry(entryMap.get(j.next()), pathwayNet);
		}
	}
	
	private CyNode mapEntry(Entry entry, CyNetwork network){
		CyNode node = createNode(entry, network);
		
		for (Graphics grap : entry.getGraphics()) {
			if (grap != null) {
				
				if(grap.getName() != null){
					String[] labels = grap.getName().split(", ");
					nodeTable.getRow(node.getSUID()).set(KEGG_LABEL, labels[0]);
					nodeTable.getRow(node.getSUID()).set(KEGG_LABEL_LIST, Arrays.asList(labels));
					network.getDefaultNodeTable().getRow(node.getSUID()).set("name", labels[0]);
				}
				
				//If not a line
				if(!grap.getType().equals("line")){
					nodeTable.getRow(node.getSUID()).set(KEGG_X, Double.parseDouble(grap.getX()));
					nodeTable.getRow(node.getSUID()).set(KEGG_Y, Double.parseDouble(grap.getY()));
					
					nodeTable.getRow(node.getSUID()).set(KEGG_WIDTH, Double.parseDouble(grap.getWidth()));
					nodeTable.getRow(node.getSUID()).set(KEGG_HEIGHT, Double.parseDouble(grap.getHeight()));
				}else{
					
					CyNode nodeTwo = createNode(entry, network);
					
					if(grap.getName() != null){
						String[] labels = grap.getName().split(", ");
						nodeTable.getRow(nodeTwo.getSUID()).set(KEGG_LABEL, labels[0]);
						nodeTable.getRow(nodeTwo.getSUID()).set(KEGG_LABEL_LIST, Arrays.asList(labels));
						network.getDefaultNodeTable().getRow(nodeTwo.getSUID()).set("name", labels[0]);
					}
					
					String[] coords = grap.getCoords().split(",");
					
					nodeTable.getRow(node.getSUID()).set(KEGG_X, Double.parseDouble(coords[0]));
					nodeTable.getRow(node.getSUID()).set(KEGG_Y, Double.parseDouble(coords[1]));
					
					nodeTable.getRow(nodeTwo.getSUID()).set(KEGG_X, Double.parseDouble(coords[2]));
					nodeTable.getRow(nodeTwo.getSUID()).set(KEGG_Y, Double.parseDouble(coords[3]));
					
					
					//Resize node size to one
					nodeTable.getRow(node.getSUID()).set(KEGG_WIDTH, Double.parseDouble("1"));
					nodeTable.getRow(node.getSUID()).set(KEGG_HEIGHT, Double.parseDouble("1"));
					
					nodeTable.getRow(nodeTwo.getSUID()).set(KEGG_WIDTH, Double.parseDouble("1"));
					nodeTable.getRow(nodeTwo.getSUID()).set(KEGG_HEIGHT, Double.parseDouble("1"));
					
					//KEGG.entry
					nodeTable.getRow(node.getSUID()).set(KEGG_ENTRY, "hiddengene");
					nodeTable.getRow(nodeTwo.getSUID()).set(KEGG_ENTRY, "hiddengene");
					
					//create edge between invisible nodes
					network.addEdge(node, nodeTwo, false);
				}
				
			}
		}
		
		return node;
	}
	
	private CyNode createNode(Entry entry, CyNetwork network){
		
		CyNode node = network.addNode();
		
		nodeTable.getRow(node.getSUID()).set(KEGG_ID, Integer.parseInt(entry.getId()));
		
		if(entry.getName() != null){
			nodeTable.getRow(node.getSUID()).set(KEGG_NAME, entry.getName());
			nodeTable.getRow(node.getSUID()).set(KEGG_NAME_LIST, Arrays.asList(entry.getName().split(" ")));
		}
		
		if (entry.getLink() != null)
			nodeTable.getRow(node.getSUID()).set(KEGG_LINK, entry.getLink());
			
		if (entry.getType() != null)
			nodeTable.getRow(node.getSUID()).set(KEGG_ENTRY, entry.getType());
		
		String reaction = entry.getReaction();
		if (reaction != null) {
			nodeTable.getRow(node.getSUID()).set(KEGG_REACTION, reaction);
			nodeTable.getRow(node.getSUID()).set(KEGG_REACTION_LIST, Arrays.asList(reaction.split(" ")));
		}
		
		return node;
	}
	
	private CyEdge createEdge(int idA, int idB){
		
		if(nodeGroupMap.containsKey(idA) && nodeGroupMap.containsKey(idB)){
			if(nodeGroupMap.get(idA).equals(nodeGroupMap.get(idB))){
				return networkMap.get(idA).addEdge(getNodeByKeggId(idA), getNodeByKeggId(idB), true);
			}else{
				return pathwayNet.addEdge(getNodeByKeggId(nodeGroupMap.get(idA)), getNodeByKeggId(nodeGroupMap.get(idB)), true);
			}
		}else if(nodeGroupMap.containsKey(idA)){
			return pathwayNet.addEdge(getNodeByKeggId(nodeGroupMap.get(idA)), getNodeByKeggId(idB), true);
		}else if(nodeGroupMap.containsKey(idB)){
			return pathwayNet.addEdge(getNodeByKeggId(idA), getNodeByKeggId(nodeGroupMap.get(idB)), true);
		}else{
			return pathwayNet.addEdge(getNodeByKeggId(idA), getNodeByKeggId(idB), true);
		}
	}
	
	private void mapReaction(Reaction reaction){
		if (pathway.getNumber().equals(METABOLIC_PATHWAYS_ENTRY_ID) || pathway.getNumber().equals(BIOSYNTHESIS_OF_SECONDARY_METABOLITES_ENTRY_ID)) {
			for (Substrate substrate : reaction.getSubstrate()) {
				for (Product product : reaction.getProduct()) {
					CyEdge edge = createEdge(Integer.parseInt(substrate.getId()), Integer.parseInt(product.getId()));
					
					edgeTable.getRow(edge.getSUID()).set(KEGG_NAME, reaction.getName());
					edgeTable.getRow(edge.getSUID()).set(KEGG_REACTION, reaction.getType());
				}
			}
		}else{
			if (reaction.getType().equals(REACTION_TYPE_IRREVERSIBLE)) {
				for (Substrate substrate : reaction.getSubstrate()) {
					CyEdge edge = createEdge(Integer.parseInt(substrate.getId()), Integer.parseInt(reaction.getId()));					
					
					edgeTable.getRow(edge.getSUID()).set(KEGG_NAME, reaction.getName());
					edgeTable.getRow(edge.getSUID()).set(KEGG_REACTION, reaction.getType());
				}
				for (Product product : reaction.getProduct()) {
					CyEdge edge = createEdge(Integer.parseInt(reaction.getId()), Integer.parseInt(product.getId()));
					
					edgeTable.getRow(edge.getSUID()).set(KEGG_NAME, reaction.getName());
					edgeTable.getRow(edge.getSUID()).set(KEGG_REACTION, reaction.getType());
				}
			} else if (reaction.getType().equals(REACTION_TYPE_REVERSIBLE)) {
				for (Substrate substrate : reaction.getSubstrate()) {
					CyEdge edge = createEdge(Integer.parseInt(reaction.getId()), Integer.parseInt(substrate.getId()));
					
					edgeTable.getRow(edge.getSUID()).set(KEGG_NAME, reaction.getName());
					edgeTable.getRow(edge.getSUID()).set(KEGG_REACTION, reaction.getType());
				}
				for (Product product : reaction.getProduct()) {
					CyEdge edge = createEdge(Integer.parseInt(reaction.getId()), Integer.parseInt(product.getId()));
					
					edgeTable.getRow(edge.getSUID()).set(KEGG_NAME, reaction.getName());
					edgeTable.getRow(edge.getSUID()).set(KEGG_REACTION, reaction.getType());
				}
			}
		}
	}
	
	private void mapRelation(Relation relation){
		//String type = relation.getType();
		
		//if (type.equals(KEGGRelationType.MAPLINK.getTag())) {
			//if map?
		//}else{
			//CyNode nodeA = getNodeByKeggId(Integer.parseInt(relation.getEntry1()));
			//CyNode nodeB = getNodeByKeggId(Integer.parseInt(relation.getEntry2()));
			
			//if(nodeA != null && nodeB != null){
				List<Subtype> subs = relation.getSubtype();
				
				for(Subtype sub : subs){
					CyEdge edge = createEdge(Integer.parseInt(relation.getEntry1()), Integer.parseInt(relation.getEntry2()));
					
					edgeTable.getRow(edge.getSUID()).set(KEGG_NAME, sub.getName());
					edgeTable.getRow(edge.getSUID()).set(KEGG_REACTION, relation.getType());
				}
			//}
		//}
	}
	
	private CyNode getNodeByKeggId(int keggId){
		Collection<CyRow> matchingRows = nodeTable.getMatchingRows(KEGG_ID, keggId);
		CyRow myRow = matchingRows.iterator().next();
		return rootNet.getNode(myRow.get("SUID", Long.class));
	}
	
	private void createTableFields(){
		nodeTable = rootNet.getDefaultNodeTable();
		nodeTable.createColumn(KEGG_NAME, String.class, false);
		nodeTable.createListColumn(KEGG_NAME_LIST, String.class, false);
		nodeTable.createColumn(KEGG_LINK, String.class, false);
		nodeTable.createColumn(KEGG_ENTRY, String.class, false);
		nodeTable.createColumn(KEGG_REACTION, String.class, false);
		nodeTable.createListColumn(KEGG_REACTION_LIST, String.class, false);
		nodeTable.createColumn(KEGG_LABEL, String.class, false);
		nodeTable.createListColumn(KEGG_LABEL_LIST, String.class, false);
		nodeTable.createColumn(KEGG_LABEL_LIST_FIRST, String.class, false);
		nodeTable.createColumn(KEGG_COLOR, String.class, false);
		nodeTable.createColumn(KEGG_WIDTH, Double.class, false);
		nodeTable.createColumn(KEGG_HEIGHT, Double.class, false);
		nodeTable.createColumn(KEGG_X, Double.class, false);
		nodeTable.createColumn(KEGG_Y, Double.class, false);
		nodeTable.createColumn(KEGG_ID, Integer.class, false);
		
		edgeTable = rootNet.getDefaultEdgeTable();
		edgeTable.createColumn(KEGG_RELATION, String.class, false);
		edgeTable.createColumn(KEGG_REACTION, String.class, false);
		edgeTable.createColumn(KEGG_NAME, String.class, false);
	}

}
