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
