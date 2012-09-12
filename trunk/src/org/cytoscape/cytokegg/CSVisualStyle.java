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

import org.cytoscape.cytokegg.generated.Graphics;
import org.cytoscape.cytokegg.util.PluginProperties;
import org.cytoscape.cytokegg.data.reader.kgml.KEGGEntryType;
import org.cytoscape.cytokegg.data.reader.kgml.KEGGRelationType;

import cytoscape.data.Semantics;
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

public class CSVisualStyle {
	
	private static final String VSNAME = PluginProperties.getInstance().getPluginName() + " - style";
	static final String KEGG_NAME = "KEGG.name";
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

	private static final Color GLOBAL_DEF_COLOR = new Color(0xAAAAAA);
		
	private static final ObjectPosition NODE_DEF_LABEL_POSITION = new ObjectPositionImpl(Position.SOUTH_EAST, Position.NORTH_WEST, Justification.JUSTIFY_CENTER, 0.0, 0.0);
	private static final ObjectPosition COMPOUND_LABEL_POSITION = new ObjectPositionImpl(Position.SOUTH_EAST, Position.NORTH_WEST, Justification.JUSTIFY_CENTER, -30.0, 3.0);
	private static final Font nodeLabelFont = new Font("SansSerif", 7, Font.PLAIN);
	
	
	//Instance
	private static CSVisualStyle instance = new CSVisualStyle();

	/**
	 * Constructor, private because of singleton
	 */
	private CSVisualStyle() {
	}

	/**
	 * Get the current instance
	 * @return CSVisualStyle
	 */
	/*public static CSVisualStyle getInstance() {
		return instance;
	}*/
	
	public String getName(){
		return VSNAME;
	}
	
	private void createStyle(String pathway_entryID){
		final VisualStyle defStyle = new VisualStyle(VSNAME);
		
		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle.getGlobalAppearanceCalculator();
		
		//Set BGColor
		gac.setDefaultBackgroundColor(Color.white);
		
		//Node Label Mapping
		final PassThroughMapping m = new PassThroughMapping("", KEGG_LABEL_LIST_FIRST);
		final Calculator nodeLabelMappingCalc = new BasicCalculator(VSNAME+ "-" + "NodeLabelMapping", m, VisualPropertyType.NODE_LABEL);
		nac.setCalculator(nodeLabelMappingCalc);
		nac.setNodeSizeLocked(false);
		
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR, NODE_DEF_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR, NODE_LINE_DEF_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 0);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR, NODE_LABEL_DEF_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_FACE, nodeLabelFont);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_SIZE, 6);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_WIDTH, 15);
		
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_SHAPE,ArrowShape.DELTA);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_OPACITY,50);
		
		//Edge Mapping
		final DiscreteMapping edgeLineStyle = new DiscreteMapping(LineStyle.SOLID, KEGG_RELATION, ObjectMapping.EDGE_MAPPING);
		final Calculator edgeLineStyleCalc = new BasicCalculator(VSNAME + "-EdgeLineStyleMapping", edgeLineStyle, VisualPropertyType.EDGE_LINE_STYLE);
		
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
		
		final DiscreteMapping edgeTgtarrowShape = new DiscreteMapping(ArrowShape.DELTA, Semantics.INTERACTION, ObjectMapping.EDGE_MAPPING);
		final Calculator edgeTgtarrowShapeCalc = new BasicCalculator(VSNAME+ "-" + "EdgeTgtarrowStyleMapping", edgeTgtarrowShape, VisualPropertyType.EDGE_TGTARROW_SHAPE);

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
		
		//Node shape
		final DiscreteMapping nodeShape = new DiscreteMapping(NodeShape.RECT, KEGG_ENTRY, ObjectMapping.NODE_MAPPING);
		final Calculator nodeShapeCalc = new BasicCalculator(VSNAME + "-NodeShapeMapping", nodeShape, VisualPropertyType.NODE_SHAPE);
		nodeShape.putMapValue(KEGGEntryType.MAP.getTag(), NodeShape.ROUND_RECT);
		nodeShape.putMapValue(KEGGEntryType.GENE.getTag(), NodeShape.RECT);
		nodeShape.putMapValue(KEGGEntryType.ORTHOLOG.getTag(), NodeShape.RECT);
		nodeShape.putMapValue(KEGGEntryType.COMPOUND.getTag(), NodeShape.ELLIPSE);
		nac.setCalculator(nodeShapeCalc);

		// Node Border Mapping
		final DiscreteMapping nodeLineWidth = new DiscreteMapping(1, KEGG_ENTRY, ObjectMapping.NODE_MAPPING);
		final Calculator nodeLineWidthCalc = new BasicCalculator(VSNAME + "-NodeBorderWidthMapping", nodeLineWidth, VisualPropertyType.NODE_LINE_WIDTH);
		nodeLineWidth.putMapValue(KEGGEntryType.MAP.getTag(), 0);
		nodeLineWidth.putMapValue(KEGGEntryType.GENE.getTag(), 2);
		nodeLineWidth.putMapValue(KEGGEntryType.ORTHOLOG.getTag(), 2);
		nodeLineWidth.putMapValue(KEGGEntryType.COMPOUND.getTag(), 0);
		nac.setCalculator(nodeLineWidthCalc);

		// Node Opacity Mapping
		final DiscreteMapping nodeOpacity = new DiscreteMapping(255, KEGG_ENTRY, ObjectMapping.NODE_MAPPING);
		final Calculator nodeOpacityCalc = new BasicCalculator(VSNAME + "-NodeOpacityMapping", nodeOpacity, VisualPropertyType.NODE_OPACITY);
		nodeOpacity.putMapValue(KEGGEntryType.MAP.getTag(), 60);
		nodeOpacity.putMapValue(KEGGEntryType.COMPOUND.getTag(), 200);
		nac.setCalculator(nodeOpacityCalc);
		
		if (pathway_entryID.equals(METABOLIC_PATHWAYS_ENTRY_ID) || pathway_entryID.equals(BIOSYNTHESIS_OF_SECONDARY_METABOLITES_ENTRY_ID)) {

			// Set edge opacity
			eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, 220);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 90);

			final DiscreteMapping nodeColorMap = new DiscreteMapping(NODE_DEF_COLOR, KEGG_COLOR, ObjectMapping.NODE_MAPPING);
			final Calculator nodeColorCalc = new BasicCalculator(VSNAME + "-NodeColorMapping", nodeColorMap, VisualPropertyType.NODE_FILL_COLOR);

			final DiscreteMapping edgeColorMap = new DiscreteMapping(EDGE_DEF_COLOR, KEGG_COLOR, ObjectMapping.EDGE_MAPPING);
			final Calculator edgeColorCalc = new BasicCalculator(VSNAME + "-EdgeColorMapping", edgeColorMap, VisualPropertyType.EDGE_COLOR);
		
		}
	
	}
	
}
