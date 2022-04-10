package app.mvc.controller.masterworker;

import app.mvc.controller.Flag;
import app.mvc.controller.task.Task;
import app.mvc.controller.task.TaskBag;
import app.mvc.controller.task.TaskCompletionLatch;
import app.util.Body;

import java.util.List;

public class WorkerAgent extends Thread {

	private final TaskBag bag;
	private final Flag stopFlag;
	private final boolean debugLogs;
	private final TaskCompletionLatch latch;

	public WorkerAgent(TaskBag bag, TaskCompletionLatch latch, Flag stopFlag, boolean debugLogs){
		this.bag = bag;
		this.latch = latch;
		this.stopFlag = stopFlag;
		this.debugLogs = debugLogs;
	}
	
	public void run(){
		log("started");
		while (stopFlag.isNotSet()) {
			Task t = bag.getATask();
			List<Body> bodies = t.getBodies();
			log("task allocated - " + bodies.size());

			for (Body b: bodies) {
				if (stopFlag.isNotSet()) {
					t.getStrategy().compute(b);
				}
				else {
					log("interrupted");
				}
			}

			latch.notifyCompletion();
			log("completed");
		}
	}
	
	private void log(String msg){
		if(debugLogs) {
			synchronized(System.out){
				System.out.println("[ worker ] " + msg);
			}
		}
	}
	
}
