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
package age.mpi.de.cytokegg.internal.task;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import age.mpi.de.cytokegg.internal.ui.GeneListImportDialog;

public class ReadFileTask implements Task{

    private File file;

    private GeneListImportDialog dialog;
    
    //Task variables
    private final String TASK_TITLE = "Reading expression file";

    public ReadFileTask(File file, GeneListImportDialog dialog) {
    	this.file = file;
        this.dialog=dialog;
	}

    @Override
    public void run(TaskMonitor taskMonitor){
    	// Give the task a title.
        taskMonitor.setTitle(TASK_TITLE);
		taskMonitor.setProgress(-1);
		
		Vector<Vector> data = new Vector<Vector>();
		String name = "";
    	
    	try{
    	
	    	FileInputStream inputStream = new FileInputStream(file);
	        DataInputStream in = new DataInputStream(inputStream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        
	        name = file.getName();
	        
	        String str;
	        while ((str = br.readLine()) != null) {
	        	String[] line = str.split("\t");
	        	
	        	Vector<Object> lineVector = new Vector<Object>();
	        	lineVector.add(line[0]);
	        		
	        	for(int i=1; i<line.length; i++){
	        		try{
	        			lineVector.add(Double.parseDouble(line[i]));
	        		}catch(NumberFormatException e){
	        			lineVector.add(line[i]);
	        		}
	        	}
	        	data.add(lineVector);
	        }
	        in.close();
	        inputStream.close();
	        br.close();
	        
	        dialog.setData(data);
	        dialog.setDsName(name);
	        dialog.initTableData();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

	@Override
	public void cancel() {
	}
}
