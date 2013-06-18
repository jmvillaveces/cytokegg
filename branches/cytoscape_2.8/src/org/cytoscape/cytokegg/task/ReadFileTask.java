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
package org.cytoscape.cytokegg.task;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import java.io.*;
import java.util.Vector;

public class ReadFileTask implements Task{

    private File file;
    private Vector<Vector> data;
    private String name;

    //Task variables
    private boolean interrupted = false;
    private TaskMonitor taskMonitor;
    private final String TASK_TITLE = "Reading expression file";
    
    public ReadFileTask(File file){
        this.file = file;
        this.data = new Vector<Vector>();
    }

    public Vector<Vector> getData(){
        return data;
    }
    
    public String getName(){
    	return name;
    }

    @Override
    public void run() {
    	
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
    	}catch(FileNotFoundException e){
    		e.printStackTrace();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void halt() {
        this.interrupted = true;
    }

    @Override
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        this.taskMonitor = taskMonitor;
    }

    @Override
    public String getTitle() {
        return TASK_TITLE;
    }
}
