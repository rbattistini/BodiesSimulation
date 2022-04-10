package app.mvc.controller;

import app.mvc.controller.task.Task;

public class StartSynchonizer {

	private boolean started;
	private Task fullJob;
	
	public StartSynchonizer(){
		started = false;
	}
	
	public synchronized Task waitStart() {
		while (!started) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		started = false;
		return fullJob;
	}

	public synchronized void notifyStarted() {
		started = true;
		fullJob = new Task();
		notifyAll();
	}
}
