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
package age.mpi.de.cytokegg.internal.model;

import java.util.Iterator;

import org.cytoscape.view.vizmap.VisualStyle;

import age.mpi.de.cytokegg.internal.CKController;

public class CKVisMapper {
	
	public CKVisMapper(){
		createMapper();
	}
	
	private void createMapper(){
		
		// If the style already existed, remove it first
		Iterator<VisualStyle> it = CKController.getInstance().getvMappingManager().getAllVisualStyles().iterator();
		while (it.hasNext()){
			VisualStyle curVS = it.next();
			if (curVS.getTitle().equalsIgnoreCase("CytoKegg visual style")){
				CKController.getInstance().getvMappingManager().removeVisualStyle(curVS);
				break;
			}
		}
		
		// Create a new Visual style
		VisualStyle vs = CKController.getInstance().getVisualStyleFactory().createVisualStyle("CytoKegg visual style");

		//CyTable attr = CKController.getInstance().getCyApplicationManager().getCurrentNetwork().getDefaultNodeTable();
		
		//DiscreteMapping dMapping = (DiscreteMapping) CKController.getInstance().getVmfFactoryP().createVisualMappingFunction("KEGG.entry", String.class, BasicVisualLexicon.NODE_SHAPE);
		
		//dMapping.putMapValue("gene", NodeShapeVisualProperty.DIAMOND);
		//vs.addVisualMappingFunction(dMapping);	
		
		CKController.getInstance().getvMappingManager().addVisualStyle(vs);
	}
	
}
