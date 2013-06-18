package org.cytoscape.cytokegg;

import giny.view.Justification;
import giny.view.NodeView;
import giny.view.ObjectPosition;
import giny.view.Position;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import org.cytoscape.cytokegg.generated.Entry;
import org.cytoscape.cytokegg.generated.Graphics;
import org.cytoscape.cytokegg.generated.Pathway;
import org.cytoscape.cytokegg.util.PluginProperties;
import org.cytoscape.cytokegg.data.reader.kgml.KEGGEntryType;
import org.cytoscape.cytokegg.data.reader.kgml.KEGGRelationType;
import org.cytoscape.cytokegg.data.reader.kgml.KEGGShape;

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

public class KeggVisualStyle {

	//Node Attributes
	private static final String PLUGIN_NAME = PluginProperties.getInstance().getPluginName();
	private static final String NAME = PLUGIN_NAME+".name";
	private static final String TYPE = PLUGIN_NAME+".type";
	private static final String COLOR = PLUGIN_NAME+".color";
		
	// Edge attributes
	private static final String RELATION = PLUGIN_NAME+".relation";
	
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
	
	// Special cases: Global Map
	private static final String METABOLIC_PATHWAYS_ENTRY_ID = "01100";
	private static final String BIOSYNTHESIS_OF_SECONDARY_METABOLITES_ENTRY_ID = "01110";
	
	//Visual Style
	private static VisualStyle style;
	private static String vsName = "";
	
	public static void create(CyNetwork network, String pathwayID, Pathway pathway, Map<String,CyNode> nodeMap, Map<String,Entry> entryMap){
		vsName = "KEGG: " + network.getTitle() + " ("+ pathwayID + ") ";
		CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
		
		//If visual style exists remove it!  (just in case)
		if(Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyleNames().contains(vsName)){
			Cytoscape.getVisualMappingManager().getCalculatorCatalog().removeVisualStyle(vsName);
		}
		
		style = new VisualStyle(vsName);
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
			DiscreteMapping nodeColorMap = new DiscreteMapping(NODE_DEF_COLOR, PluginProperties.getInstance().getPluginName()+".exp.maped", ObjectMapping.NODE_MAPPING);
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
		
		CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		for (String key : nodeMap.keySet()) {

			for (Graphics nodeGraphics : entryMap.get(key).getGraphics()) {
				if (KEGGShape.getShape(nodeGraphics.getType()) != -1) {
					String nodeID = nodeMap.get(key).getIdentifier();

					NodeView nv = view.getNodeView(nodeMap.get(key));
					if (nv == null)
						continue;

					nv.setXPosition(Double.parseDouble(nodeGraphics.getX()));
					nv.setYPosition(Double.parseDouble(nodeGraphics.getY()));

					double w = Double.parseDouble(nodeGraphics.getWidth());
					nodeAttr.setAttribute(nodeID, "KEGG.nodeWidth", w);

					nodeWidth.putMapValue(nodeID, w);

					double h = Double.parseDouble(nodeGraphics.getHeight());
					nodeAttr.setAttribute(nodeID, "KEGG.nodeHeight", h);

					nodeHeight.putMapValue(nodeID, h);
					nv.setShape(KEGGShape.getShape(nodeGraphics.getType()));
				}
			}
		}
	}
	
	public static VisualStyle getStyle(){
		return style;
	}
	
	public static String getName(){
		return vsName;
	}
}
