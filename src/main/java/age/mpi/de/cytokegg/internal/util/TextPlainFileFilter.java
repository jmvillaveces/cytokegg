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
package age.mpi.de.cytokegg.internal.util;

import java.io.File;

import javax.activation.FileDataSource;
import javax.swing.filechooser.FileFilter;

public class TextPlainFileFilter extends FileFilter {

    public boolean accept(File file) {
        FileDataSource dataSource = new FileDataSource(file);

        if(dataSource.getContentType().equals("text/plain")){
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Just text/plain mime type";
    }
}
