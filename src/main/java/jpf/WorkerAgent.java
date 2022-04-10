package jpf;

import gov.nasa.jpf.vm.Verify;
import app.mvc.controller.Flag;
import app.mvc.controller.task.Task;
import app.mvc.controller.task.TaskBag;
import app.mvc.controller.task.TaskCompletionLatch;
import app.util.Body;

import java.util.List;

public class WorkerAgent extends Thread {

	private final TaskBag bag;
	private final Flag stopFlag;
	private final TaskCompletionLatch latch;

	public WorkerAgent(TaskBag bag, TaskCompletionLatch latch, Flag stopFlag){
		this.bag = bag;
		this.latch = latch;
		this.stopFlag = stopFlag;
	}
	
	public void run(){
		while (stopFlag.isNotSet()) {
			Task t = bag.getATask();
			List<Body> bodies = t.getBodies();

			for (Body b: bodies) {
				if (stopFlag.isNotSet()) {
					Verify.beginAtomic();
					t.getStrategy().compute(b);
					Verify.endAtomic();
				}
			}

			latch.notifyCompletion();
		}
	}
}
