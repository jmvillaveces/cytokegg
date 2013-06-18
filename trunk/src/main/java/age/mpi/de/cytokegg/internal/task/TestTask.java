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
