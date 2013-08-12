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
package age.mpi.de.cytokegg.internal.ui;

public enum DialogEnum {
	DATASET(0), FILEIMPORT(1), NETWORKIMPORT(2), PATHWAYSELECTION(3), BROWSEPATHWAYDIALOG(4), REPOSITORY(5);
	
	private int tag;
	
	private DialogEnum(final int tag) {
		this.tag = tag;
	}
	
	public int getTag() {
		return this.tag;
	}
	
	public static DialogEnum getType(final int tag) {
		for(DialogEnum entry: DialogEnum.values()) {
			if(entry.getTag() == tag)
				return entry;
		}
		return null;
	}
	
}
