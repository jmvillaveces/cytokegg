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
package org.cytoscape.cytokegg.data;

public class PicrAccession {

    private String accession;
    private String databaseDescription;
    private String dataBaseName;
    private String refType;

    public PicrAccession(String accession, String databaseDescription, String dataBaseName, String refType) {
        this.accession = accession;
        this.databaseDescription = databaseDescription;
        this.dataBaseName = dataBaseName;
        this.refType = refType;
    }

    public String getAccession() {
        return accession;
    }

    public String getDatabaseDescription() {
        return databaseDescription;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getRefType() {
        return refType;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public void setDatabaseDescription(String databaseDescription) {
        this.databaseDescription = databaseDescription;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }
}
