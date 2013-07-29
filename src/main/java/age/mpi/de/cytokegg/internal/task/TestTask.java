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

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class TestTask implements Task {

	@Override
	public void cancel() {
		int i=0;
		while(i<5){
			System.out.println("cancelling!");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		while(true){
			System.out.println("running!");
			Thread.sleep(1000);
		}
	}

	

}
