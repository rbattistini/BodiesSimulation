package app.mvc.controller.task;

public class TaskCompletionLatch {

	private final int nWorkers;
	private int nCompletionsNotified;
	
	public TaskCompletionLatch(int nWorkers){
		this.nWorkers = nWorkers;
		nCompletionsNotified = 0;
	}
	
	public synchronized void reset() {
		nCompletionsNotified = 0;	
	}
	
	public synchronized void waitCompletion() throws InterruptedException {
		while (nCompletionsNotified < nWorkers) {
			wait();
		}
	}

	public synchronized void notifyCompletion() {
		nCompletionsNotified++;
		notifyAll();
	}
	
}
