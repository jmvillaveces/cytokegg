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

import giny.view.Justification;
import giny.view.NodeView;
import giny.view.ObjectPosition;
import giny.view.Position;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.cytokegg.data.reader.kgml.KEGGEntryType;
import org.cytoscape.cytokegg.data.reader.kgml.KEGGRelationType;
import org.cytoscape.cytokegg.generated.Component;
import org.cytoscape.cytokegg.generated.Entry;
import org.cytoscape.cytokegg.generated.Graphics;
import org.cytoscape.cytokegg.generated.Product;
import org.cytoscape.cytokegg.generated.Reaction;
import org.cytoscape.cytokegg.generated.Relation;
import org.cytoscape.cytokegg.generated.Substrate;
import org.cytoscape.cytokegg.generated.Subtype;
import org.cytoscape.cytokegg.util.PluginProperties;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import ding.view.ObjectPositionImpl;

public class CSPathwayMapper {
	
	// Node Attributes
	private static final String PLUGIN_NAME = PluginProperties.getInstance().getPluginName();
	private static final String NAME = PLUGIN_NAME+".name";
	private static final String NAMES = PLUGIN_NAME+".names";
	private static final String KEGG_NAME = PLUGIN_NAME+".kegg.name";
	private static final String KEGG_NAMES = PLUGIN_NAME+".kegg.names";
	private static final String TYPE = PLUGIN_NAME+".type";
	private static final String LINK = PLUGIN_NAME+".link";
	private static final String KEGG_REACTION = PLUGIN_NAME+".reaction";
	private static final String KEGG_REACTIONS = PLUGIN_NAME+".reactions";
	private static final String COLOR = PLUGIN_NAME+".color";
	
	// Edge attributes
	private static final String RELATION = PLUGIN_NAME+".relation";
	
	// Special cases: Global Map
	private static final String METABOLIC_PATHWAYS_ENTRY_ID = "01100";
	private static final String BIOSYNTHESIS_OF_SECONDARY_METABOLITES_ENTRY_ID = "01110";
	
	// Default Values for Visual Style
	private static final Color NODE_DEF_COLOR = Color.WHITE;
	private static final Color EDGE_DEF_COLOR = new Color(50, 50, 50);
	private static final Color NODE_LINE_DEF_COLOR = new Color(20, 20, 20);
	private static final Color NODE_LABEL_DEF_COLOR = new Color(30, 30, 30);

	private static final Color GENE_COLOR = new Color(153, 255, 153);
	private static final Color COMPOUND_COLOR = new Color(0xAAAAEE);
	private static final Color MAP_COLOR = new Color(0x00BFFF);
	private static final Color UP = Color.red;
	private static final Color DOWN = Color.blue;
	private static final Color NONDE = Color.green;

	private static final Color GLOBAL_DEF_COLOR = new Color(0xAAAAAA);
					
	private static final ObjectPosition NODE_DEF_LABEL_POSITION = new ObjectPositionImpl(Position.SOUTH_EAST, Position.NORTH_WEST, Justification.JUSTIFY_CENTER, 0.0, 0.0);
	private static final ObjectPosition COMPOUND_LABEL_POSITION = new ObjectPositionImpl(Position.SOUTH_EAST, Position.NORTH_WEST, Justification.JUSTIFY_CENTER, -30.0, 3.0);

	private static final Font nodeLabelFont = new Font("SansSerif", 7, Font.PLAIN);
	
	private org.cytoscape.cytokegg.generated.Pathway pathway;
	private String pathwayID;
	private Map<String, CyNode> nodeMap;
	private Map<String, Entry> entryMap;
	private Map<String, CyNetwork> nodeNetworkMap;
	private Map<String, String> nodeGroupMap;
	private List<CyEdge> edges;
	private CyAttributes nodeAttr;
	
	public CSPathwayMapper(final org.cytoscape.cytokegg.generated.Pathway pathway){
		this.pathway = pathway;
		this.pathwayID = pathway.getName();
		
		nodeAttr = Cytoscape.getNodeAttributes();
		nodeMap = new HashMap<String,CyNode>();
		entryMap = new HashMap<String,Entry>();
		nodeGroupMap = new HashMap<String, String>();
		nodeNetworkMap = new HashMap<String, CyNetwork>();
		edges = new ArrayList<CyEdge>();
	}
	
	public CyNetwork doMapping(){
		
		mapNodes();
		mapRelations();
		mapReactions();
		
		CyNetwork network = Cytoscape.createNetwork(pathwayID + "-"+ pathway.getTitle());
		
		//Add nodes
		for(String key : nodeMap.keySet()){
			CyNode node = nodeMap.get(key);
			network.addNode(node);
		}
		
		//Add edges
		for(CyEdge edge : edges){
			network.addEdge(edge);
		}
		
		//set nested networks
		for(String key : nodeNetworkMap.keySet()){
			CyNode node = nodeMap.get(key);
			node.setNestedNetwork(nodeNetworkMap.get(key));
		}
		return network;
	}
	
	private void mapNodes(){
		
		for(Entry eNode : pathway.getEntry()){
			
			entryMap.put(eNode.getId(), eNode);
			
			CyNode node = Cytoscape.getCyNode(pathwayID + "-"+ eNode.getId(), true);
			
			//Set node attributes
			nodeAttr.setAttribute(node.getIdentifier(), TYPE, eNode.getType());
			nodeAttr.setListAttribute(node.getIdentifier(), KEGG_NAMES, Arrays.asList(eNode.getName().split(" ")));
			nodeAttr.setAttribute(node.getIdentifier(), KEGG_NAME, eNode.getName().split(" ")[0]);
			
			if (eNode.getLink() != null)
				nodeAttr.setAttribute(node.getIdentifier(), LINK, eNode.getLink());
			
			String reaction = eNode.getReaction();
			// Save reaction
			if (reaction != null) {
				nodeAttr.setAttribute(node.getIdentifier(), KEGG_REACTION, reaction);
				nodeAttr.setListAttribute(node.getIdentifier(), KEGG_REACTIONS, Arrays.asList(reaction.split(" ")));
			}
			
			if(eNode.getGraphics().size() > 0){
				Graphics graphics = eNode.getGraphics().get(0);
				
				if(graphics.getName() != null){
					nodeAttr.setListAttribute(node.getIdentifier(), NAMES, Arrays.asList(graphics.getName().split(", ")));
					nodeAttr.setAttribute(node.getIdentifier(), NAME, graphics.getName().split(", ")[0]);
				}
				
				double w = Double.parseDouble(graphics.getWidth());
				nodeAttr.setAttribute(node.getIdentifier(), "KEGG.nodeWidth", w);

				double h = Double.parseDouble(graphics.getHeight());
				nodeAttr.setAttribute(node.getIdentifier(), "KEGG.nodeHeight", h);
				
				double x = Double.parseDouble(graphics.getX());
				nodeAttr.setAttribute(node.getIdentifier(), "KEGG.position.x", x);
				
				double y = Double.parseDouble(graphics.getY());
				nodeAttr.setAttribute(node.getIdentifier(), "KEGG.position.y", y);

			}
			
			if (eNode.getType().equals(KEGGEntryType.GENE.getTag()) || eNode.getType().equals(KEGGEntryType.ORTHOLOG.getTag())){
				DataSet gList = Plugin.getInstance().getCurrentDataSet();
				if(gList != null){
					for(String guId : gList.getGenes()){
						String[] namesArr = eNode.getName().split(" ");
						
						
						if(Arrays.asList(namesArr).contains(guId)){
							
							
							String[] conditions = new String[gList.getConditions().length];
							Double[] values = gList.getExpression(guId); 
							String[] strValues = new String[gList.getConditions().length];
							
							int i=0;
							for(String condition : gList.getConditions()){
								conditions[i] = condition;
								strValues[i] = values[i]+"";
								
								nodeAttr.setAttribute(node.getIdentifier(), PluginProperties.getInstance().getPluginName()+"."+condition, values[i]);
								i++;
							}
							
							//Add expression attributes
							nodeAttr.setListAttribute(node.getIdentifier(), PluginProperties.getInstance().getPluginName()+".conditions", Arrays.asList(conditions));
							nodeAttr.setListAttribute(node.getIdentifier(), PluginProperties.getInstance().getPluginName()+".values", Arrays.asList(strValues));
						}
					}
				}
			}else if(eNode.getType().equals(KEGGEntryType.GROUP.getTag())){
				
				CyNetwork nNetwork = Cytoscape.createNetwork(pathwayID + "-"+eNode.getId());
				List<CyNode> nodeLst = new ArrayList<CyNode>();
				for(Component c : eNode.getComponent()){
					nodeLst.add(nodeMap.get(c.getId()));
					nNetwork.addNode(nodeMap.get(c.getId()));
					nodeMap.remove(c.getId());
					nodeGroupMap.put(c.getId(), eNode.getId());
				}
				updateView(nNetwork);
				nodeNetworkMap.put(eNode.getId(), nNetwork);
				//node.setNestedNetwork(nNetwork);
			}
			nodeMap.put(eNode.getId(), node);
		}
	}
	
	private void mapRelations() {
		List<Relation> relations = pathway.getRelation();
		
		CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		for (Relation rel : relations) {
			String type = rel.getType();
			
			CyNode nodeOne = (nodeGroupMap.containsKey(rel.getEntry1())) ? nodeMap.get(nodeGroupMap.get(rel.getEntry1())) : nodeMap.get(rel.getEntry1());
			CyNode nodeTwo = (nodeGroupMap.containsKey(rel.getEntry2())) ? nodeMap.get(nodeGroupMap.get(rel.getEntry2())) : nodeMap.get(rel.getEntry2());
			
			List<Subtype> subs = rel.getSubtype();
			for(Subtype sub : subs){
				CyEdge edge;
				if(type.equals(KEGGRelationType.MAPLINK.getTag())){
					edge = Cytoscape.getCyEdge(nodeOne, nodeTwo, Semantics.INTERACTION, type, true, true);
					edgeAttr.setAttribute(edge.getIdentifier(), RELATION, type);
				}else{
					edge = Cytoscape.getCyEdge(nodeOne, nodeTwo, Semantics.INTERACTION, sub.getName(), true, true);
					edgeAttr.setAttribute(edge.getIdentifier(), RELATION, sub.getName());
				}
				edges.add(edge);
			}
		}
	}
	
	private void mapReactions(){
		List<Reaction> reactions = pathway.getReaction();
		String pathway_entryID = pathway.getNumber();
		
		CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		if (pathway_entryID.equals(METABOLIC_PATHWAYS_ENTRY_ID) || pathway_entryID.equals(BIOSYNTHESIS_OF_SECONDARY_METABOLITES_ENTRY_ID)) {
			for (Reaction reaction : reactions) {
				Entry entry = entryMap.get(reaction.getId());
				for (Graphics grap : entry.getGraphics()) {
					for (Substrate sub : reaction.getSubstrate()) {
						CyNode subNode = nodeMap.get(sub.getId());
						for (Product pro : reaction.getProduct()) {
							CyNode proNode = nodeMap.get(pro.getId());
							
							CyEdge edge = Cytoscape.getCyEdge(subNode, proNode,Semantics.INTERACTION, "cc", true);
							edges.add(edge);
							
							edgeAttr.setAttribute(edge.getIdentifier(), KEGG_NAME, entry.getName());
							edgeAttr.setAttribute(edge.getIdentifier(), KEGG_REACTION, entry.getReaction());
							edgeAttr.setAttribute(edge.getIdentifier(), TYPE, entry.getType());
							edgeAttr.setAttribute(edge.getIdentifier(), LINK, entry.getLink());
							edgeAttr.setAttribute(edge.getIdentifier(), COLOR, grap.getFgcolor());
						}
					}
				}
			}
		}
	}
	
	public void updateView(CyNetwork network) {
		
		String vsName = "CYTO_KEGG";//"KEGG: " + network.getTitle() + " ("+ pathwayID + ") ";
		CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
		
		CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		for (String key : nodeMap.keySet()) {
			
			CyNode node = nodeMap.get(key);
			
			NodeView nv = view.getNodeView(node);
			if (nv == null)
				continue;
			
			nv.setXPosition(nodeAttr.getDoubleAttribute(node.getIdentifier(), "KEGG.position.x"));
			nv.setYPosition(nodeAttr.getDoubleAttribute(node.getIdentifier(), "KEGG.position.y"));
		}
		
		//If visual style exists, apply it and exit
		if(Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyleNames().contains(vsName)){
			final VisualStyle targetStyle = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(vsName);
			Cytoscape.getVisualMappingManager().setVisualStyle(targetStyle);
			view.setVisualStyle(targetStyle.getName());

			Cytoscape.getVisualMappingManager().setNetworkView(view);
			view.redrawGraph(false, true);
			return;
		}
		
		VisualStyle style = new VisualStyle(vsName);
		String pathway_entryID = pathway.getNumber();
		
		GlobalAppearanceCalculator gac = style.getGlobalAppearanceCalculator();
		NodeAppearanceCalculator nac = style.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = style.getEdgeAppearanceCalculator();
		
		gac.setDefaultBackgroundColor(Color.white);
		
		//Node Label Mapping
		PassThroughMapping m = new PassThroughMapping("", NAME);
		Calculator nodeLabelMappingCalc = new BasicCalculator(vsName+ "-" + "NodeLabelMapping", m, VisualPropertyType.NODE_LABEL);
		nac.setCalculator(nodeLabelMappingCalc);
		nac.setNodeSizeLocked(false);
		
		//Node Default Props
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR, NODE_DEF_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR, NODE_LINE_DEF_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 0);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR, NODE_LABEL_DEF_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_FACE, nodeLabelFont);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_SIZE, 6);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_WIDTH, 15);
		
		//Edge Mapping
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_SHAPE,ArrowShape.DELTA);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_OPACITY,50);
				
		DiscreteMapping edgeLineStyle = new DiscreteMapping(LineStyle.SOLID, RELATION, ObjectMapping.EDGE_MAPPING);
		Calculator edgeLineStyleCalc = new BasicCalculator(vsName + "-EdgeLineStyleMapping", edgeLineStyle, VisualPropertyType.EDGE_LINE_STYLE);
				
		edgeLineStyle.putMapValue(KEGGRelationType.MAPLINK.getTag(), LineStyle.LONG_DASH);
		edgeLineStyle.putMapValue(KEGGRelationType.ACTIVATION.getTag(), LineStyle.SOLID);
		edgeLineStyle.putMapValue(KEGGRelationType.INHIBITION.getTag(), LineStyle.SOLID);
		edgeLineStyle.putMapValue(KEGGRelationType.EXPRESSION.getTag(), LineStyle.SOLID);
		edgeLineStyle.putMapValue(KEGGRelationType.REPRESION.getTag(), LineStyle.SOLID);
		edgeLineStyle.putMapValue(KEGGRelationType.IN_EFFECT.getTag(), LineStyle.EQUAL_DASH);
		edgeLineStyle.putMapValue(KEGGRelationType.STATE_CHANGE.getTag(), LineStyle.LONG_DASH);
		edgeLineStyle.putMapValue(KEGGRelationType.BINDING.getTag(), LineStyle.SOLID);
		edgeLineStyle.putMapValue(KEGGRelationType.DISSOCIATION.getTag(), LineStyle.SINEWAVE);
		edgeLineStyle.putMapValue(KEGGRelationType.MISSING_INTERACTION.getTag(), LineStyle.DOT);
		edgeLineStyle.putMapValue(KEGGRelationType.PHOSPHORYLATION.getTag(), LineStyle.DASH_DOT);
		edgeLineStyle.putMapValue(KEGGRelationType.DEPHOSPHORYLATION.getTag(), LineStyle.DASH_DOT);
		edgeLineStyle.putMapValue(KEGGRelationType.GLYCOSYLATION.getTag(), LineStyle.DASH_DOT);
		edgeLineStyle.putMapValue(KEGGRelationType.UBIQUITINATION.getTag(), LineStyle.DASH_DOT);
		edgeLineStyle.putMapValue(KEGGRelationType.METHYLATION.getTag(), LineStyle.DASH_DOT);
				
		eac.setCalculator(edgeLineStyleCalc);

		DiscreteMapping edgeTgtarrowShape = new DiscreteMapping(ArrowShape.DELTA, Semantics.INTERACTION, ObjectMapping.EDGE_MAPPING);
		Calculator edgeTgtarrowShapeCalc = new BasicCalculator(vsName+ "-" + "EdgeTgtarrowStyleMapping", edgeTgtarrowShape, VisualPropertyType.EDGE_TGTARROW_SHAPE);
		
		edgeTgtarrowShape.putMapValue("cr", ArrowShape.NONE);
		edgeTgtarrowShape.putMapValue("maplink", ArrowShape.NONE);
		if (pathway_entryID.equals(METABOLIC_PATHWAYS_ENTRY_ID) || pathway_entryID.equals(BIOSYNTHESIS_OF_SECONDARY_METABOLITES_ENTRY_ID)) {
			edgeTgtarrowShape.putMapValue("cc", ArrowShape.NONE);
		}

		edgeTgtarrowShape.putMapValue(KEGGRelationType.MAPLINK.getTag(), ArrowShape.DELTA);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.ACTIVATION.getTag(), ArrowShape.ARROW);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.INHIBITION.getTag(), ArrowShape.T);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.EXPRESSION.getTag(), ArrowShape.ARROW);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.REPRESION.getTag(), ArrowShape.ARROW);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.IN_EFFECT.getTag(), ArrowShape.DELTA);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.STATE_CHANGE.getTag(), ArrowShape.NONE);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.BINDING.getTag(), ArrowShape.NONE);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.DISSOCIATION.getTag(), ArrowShape.NONE);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.MISSING_INTERACTION.getTag(), ArrowShape.ARROW);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.PHOSPHORYLATION.getTag(), ArrowShape.HALF_ARROW_TOP);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.DEPHOSPHORYLATION.getTag(), ArrowShape.HALF_ARROW_BOTTOM);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.GLYCOSYLATION.getTag(), ArrowShape.HALF_ARROW_TOP);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.UBIQUITINATION.getTag(), ArrowShape.HALF_ARROW_TOP);
		edgeTgtarrowShape.putMapValue(KEGGRelationType.METHYLATION.getTag(), ArrowShape.HALF_ARROW_TOP);
		
		eac.setCalculator(edgeTgtarrowShapeCalc);
		
		DiscreteMapping nodeShape = new DiscreteMapping(NodeShape.RECT, TYPE, ObjectMapping.NODE_MAPPING);
		Calculator nodeShapeCalc = new BasicCalculator(vsName + "-" + "NodeShapeMapping", nodeShape, VisualPropertyType.NODE_SHAPE);
		nodeShape.putMapValue(KEGGEntryType.MAP.getTag(), NodeShape.ROUND_RECT);
		nodeShape.putMapValue(KEGGEntryType.GENE.getTag(), NodeShape.RECT);
		nodeShape.putMapValue(KEGGEntryType.ORTHOLOG.getTag(), NodeShape.RECT);
		nodeShape.putMapValue(KEGGEntryType.COMPOUND.getTag(), NodeShape.ELLIPSE);
		nac.setCalculator(nodeShapeCalc);

		// Node Border Mapping
		DiscreteMapping nodeLineWidth = new DiscreteMapping(1, TYPE, ObjectMapping.NODE_MAPPING);
		Calculator nodeLineWidthCalc = new BasicCalculator(vsName + "-" + "NodeBorderWidthMapping", nodeLineWidth, VisualPropertyType.NODE_LINE_WIDTH);
		nodeLineWidth.putMapValue(KEGGEntryType.MAP.getTag(), 0);
		nodeLineWidth.putMapValue(KEGGEntryType.GENE.getTag(), 2);
		nodeLineWidth.putMapValue(KEGGEntryType.ORTHOLOG.getTag(), 2);
		nodeLineWidth.putMapValue(KEGGEntryType.COMPOUND.getTag(), 0);
		nac.setCalculator(nodeLineWidthCalc);

		// Node Opacity Mapping
		DiscreteMapping nodeOpacity = new DiscreteMapping(255, TYPE, ObjectMapping.NODE_MAPPING);
		Calculator nodeOpacityCalc = new BasicCalculator(vsName + "-" + "NodeOpacityMapping", nodeOpacity, VisualPropertyType.NODE_OPACITY);
		nodeOpacity.putMapValue(KEGGEntryType.MAP.getTag(), 60);
		nodeOpacity.putMapValue(KEGGEntryType.COMPOUND.getTag(), 200);
		nac.setCalculator(nodeOpacityCalc);
		
		// Special Case: Global Map (Metabolic Pathway)
		if (pathway_entryID.equals(METABOLIC_PATHWAYS_ENTRY_ID) || pathway_entryID.equals(BIOSYNTHESIS_OF_SECONDARY_METABOLITES_ENTRY_ID)) {

			// Set edge opacity
			eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, 220);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 90);

			DiscreteMapping nodeColorMap = new DiscreteMapping(NODE_DEF_COLOR, COLOR, ObjectMapping.NODE_MAPPING);
			Calculator nodeColorCalc = new BasicCalculator(vsName + "-"+ "NodeColorMapping", nodeColorMap, VisualPropertyType.NODE_FILL_COLOR);

			DiscreteMapping edgeColorMap = new DiscreteMapping(EDGE_DEF_COLOR, COLOR, ObjectMapping.EDGE_MAPPING);
			Calculator edgeColorCalc = new BasicCalculator(vsName + "-"+ "EdgeColorMapping", edgeColorMap, VisualPropertyType.EDGE_COLOR);

			for (String key : nodeMap.keySet()) {
				for (Graphics nodeGraphics : entryMap.get(key).getGraphics()) {
					if (!nodeGraphics.getBgcolor().equals("none")) {
						Color c = Color.decode(nodeGraphics.getBgcolor());
						nodeColorMap.putMapValue(nodeGraphics.getBgcolor(), c);
						edgeColorMap.putMapValue(nodeGraphics.getBgcolor(), c);
					}
				}
			}

			nodeColorMap.putMapValue("none", GLOBAL_DEF_COLOR);

			DiscreteMapping edgeWidthMap = new DiscreteMapping(3, Semantics.INTERACTION, ObjectMapping.EDGE_MAPPING);
			Calculator edgeWidthCalc = new BasicCalculator(vsName + "-" + "EdgeWidthMapping", edgeWidthMap, VisualPropertyType.EDGE_LINE_WIDTH);
			edgeWidthMap.putMapValue("cc", 7);

			nac.setCalculator(nodeColorCalc);
			eac.setCalculator(edgeColorCalc);
			eac.setCalculator(edgeWidthCalc);

		} else {
			DiscreteMapping nodeColorMap = new DiscreteMapping(NODE_DEF_COLOR, TYPE, ObjectMapping.NODE_MAPPING);
			Calculator nodeColorCalc = new BasicCalculator(vsName + "-"+ "NodeColorMapping", nodeColorMap, VisualPropertyType.NODE_FILL_COLOR);
			nodeColorMap.putMapValue(KEGGEntryType.GENE.getTag(), GENE_COLOR);
			nodeColorMap.putMapValue(KEGGEntryType.COMPOUND.getTag(),COMPOUND_COLOR);
			nodeColorMap.putMapValue(KEGGEntryType.MAP.getTag(), MAP_COLOR);
			nodeColorMap.putMapValue("up", UP);
			nodeColorMap.putMapValue("down", DOWN);
			nodeColorMap.putMapValue("non-d.e", NONDE);
			
					
			DiscreteMapping nodeLabelPositionMap = new DiscreteMapping(NODE_DEF_LABEL_POSITION, TYPE, ObjectMapping.NODE_MAPPING);
			Calculator nodeLabelPositionCalc = new BasicCalculator(vsName + "-" + "NodeLabelPositionMapping", nodeLabelPositionMap, VisualPropertyType.NODE_LABEL_POSITION);
			nodeLabelPositionMap.putMapValue(KEGGEntryType.COMPOUND.getTag(), COMPOUND_LABEL_POSITION);
					
			nac.setCalculator(nodeColorCalc);
			nac.setCalculator(nodeLabelPositionCalc);
		}
		
		DiscreteMapping nodeWidth = new DiscreteMapping(30, "ID", ObjectMapping.NODE_MAPPING);
		Calculator nodeWidthCalc = new BasicCalculator(vsName + "-"+ "NodeWidthMapping", nodeWidth, VisualPropertyType.NODE_WIDTH);
		DiscreteMapping nodeHeight = new DiscreteMapping(30, "ID", ObjectMapping.NODE_MAPPING);
		Calculator nodeHeightCalc = new BasicCalculator(vsName + "-"+ "NodeHeightMapping", nodeHeight, VisualPropertyType.NODE_HEIGHT);
		
		nac.setCalculator(nodeHeightCalc);
		nac.setCalculator(nodeWidthCalc);
		
		/*CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		for (String key : nodeMap.keySet()) {
			
			CyNode node = nodeMap.get(key);
			
			NodeView nv = view.getNodeView(node);
			if (nv == null)
				continue;
			
			nv.setXPosition(nodeAttr.getDoubleAttribute(node.getIdentifier(), "KEGG.position.x"));
			nv.setYPosition(nodeAttr.getDoubleAttribute(node.getIdentifier(), "KEGG.position.y"));
			
			nodeWidth.putMapValue(node.getIdentifier(), nodeAttr.getDoubleAttribute(node.getIdentifier(), "KEGG.nodeWidth"));
			nodeHeight.putMapValue(node.getIdentifier(), nodeAttr.getDoubleAttribute(node.getIdentifier(), "KEGG.nodeHeight"));
		}*/
		
		// Add new style and repaint graph
		Cytoscape.getVisualMappingManager().getCalculatorCatalog().addVisualStyle(style);
		Cytoscape.getVisualMappingManager().setVisualStyle(style);
		view.setVisualStyle(style.getName());

		Cytoscape.getVisualMappingManager().setNetworkView(view);
		view.redrawGraph(false, true);
	}
	
	public void bypassAttributes(final CyNetwork network){
		CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
		cyNodeAttrs.deleteAttribute(VisualPropertyType.NODE_HEIGHT.getBypassAttrName());
		
		List<CyNode> nodes = Cytoscape.getCyNodesList();
		for(CyNode node : nodes){
			
			String height = cyNodeAttrs.getAttribute(node.getIdentifier(), "KEGG.nodeHeight").toString();
			cyNodeAttrs.setAttribute(node.getIdentifier(), VisualPropertyType.NODE_HEIGHT.getBypassAttrName(), height);
			
			String width = cyNodeAttrs.getAttribute(node.getIdentifier(), "KEGG.nodeWidth").toString();
			cyNodeAttrs.setAttribute(node.getIdentifier(), VisualPropertyType.NODE_WIDTH.getBypassAttrName(), width);
			
			//String x = cyNodeAttrs.getAttribute(node.getIdentifier(), "KEGG.position.x").toString();
			//cyNodeAttrs.setAttribute(node.getIdentifier(), VisualPropertyType..getBypassAttrName(), width);
			
		}
		Cytoscape.getNetworkView(network.getIdentifier()).redrawGraph(false, true);
	}
	
	public void calculateExpression(final CyNetwork network, String condition, double min, double max){
		CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
		cyNodeAttrs.deleteAttribute(VisualPropertyType.NODE_FILL_COLOR.getBypassAttrName());
		
		List<CyNode> nodes = Cytoscape.getCyNodesList();
		for(CyNode node : nodes){
			String expMapType = "";
			if(!condition.equals("")){
				Object exp = cyNodeAttrs.getAttribute(node.getIdentifier(), PluginProperties.getInstance().getPluginName()+"."+condition);
				if(exp != null){
					double expd = Double.parseDouble(exp.toString());
					if(expd > max){
						expMapType = "red";
					}else if(expd < min){
						expMapType = "blue";
					}else{
						expMapType = "green";
					}
				}
			}
			cyNodeAttrs.setAttribute(node.getIdentifier(), VisualPropertyType.NODE_FILL_COLOR.getBypassAttrName(), expMapType);
		}
		Cytoscape.getNetworkView(network.getIdentifier()).redrawGraph(false, true);
	}
}
