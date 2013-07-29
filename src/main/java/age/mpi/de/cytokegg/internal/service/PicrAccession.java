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
package age.mpi.de.cytokegg.internal.service;

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
