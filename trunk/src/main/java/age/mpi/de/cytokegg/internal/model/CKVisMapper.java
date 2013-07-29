/**
 * Copyright 2013 José María Villaveces Max Planck institute for biology of
 * ageing (MPI-age)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
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
