/*
* Copyright (C) 2011-2012 José María Villaveces
* Max Plank institute for biology of ageing (MPI-age)
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.cytoscape.cytokegg.task;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import org.cytoscape.cytokegg.CSPReader;
import org.cytoscape.cytokegg.data.reader.kgml.KGMLReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PaintPathwayTask implements Task {

    private String pathwayId;

    //Task variables
    private boolean interrupted = false;
    private TaskMonitor taskMonitor;
    private final String TASK_TITLE = "Painting the selected pathway";

    public PaintPathwayTask(String pathwayId){
        this.pathwayId = pathwayId;
    }

    public void run() {
        if (taskMonitor == null) {
            throw new IllegalStateException("Task Monitor is not set.");
        }

        if(!interrupted){
        	try {
				CSPReader read = new CSPReader(new URL("http://www.genome.jp/kegg-bin/download?entry="+pathwayId+"&format=kgml"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public void halt() {
        this.interrupted = true;
    }

    public void setTaskMonitor(TaskMonitor taskMonitor){
        if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return TASK_TITLE;
    }
}
