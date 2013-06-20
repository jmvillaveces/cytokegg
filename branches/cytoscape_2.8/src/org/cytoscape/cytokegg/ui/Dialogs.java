/*
 * Copyright (C) 2011-2012 Jos� Mar�a Villaveces Max Plank institute for biology
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
package org.cytoscape.cytokegg.ui;

public enum Dialogs {
	DATASET(0), FILEIMPORT(1), NETWORKIMPORT(2), PATHWAYSELECTION(3), BROWSEPATHWAYDIALOG(4);
	
	private int tag;
	
	private Dialogs(final int tag) {
		this.tag = tag;
	}
	
	public int getTag() {
		return this.tag;
	}
	
	public static Dialogs getType(final int tag) {
		for(Dialogs entry: Dialogs.values()) {
			if(entry.getTag() == tag)
				return entry;
		}
		return null;
	}
	
}