package app.mvc.controller.masterworker;

import app.mvc.controller.Flag;
import app.mvc.controller.StartSynchonizer;
import app.mvc.controller.task.Task;
import app.mvc.controller.task.TaskBag;
import app.mvc.controller.task.TaskCompletionLatch;
import app.mvc.model.SimModel;
import app.mvc.view.SimView;
import app.util.Body;

import java.util.*;

public class MasterAgent extends Thread implements StateObserver {

	private static final int MS_BETWEEN_FRAMES = 1;
	private final SimModel model;
	private final Flag stopFlag;
	private final StartSynchonizer synchonizer;
	private final int nWorkers;
	private final boolean debugLogs;
	private final ArrayList<SimView> listeners;
	private final TaskBag bag;
	private final TaskCompletionLatch taskLatch;
	private final List<List<Body>> bodiesToTask;
	private final Set<WorkerAgent> workers;

	public MasterAgent(SimModel model, StartSynchonizer synchonizer, Flag stopFlag, int nWorkers, boolean debugLogs){
		this.model = model;
		this.synchonizer = synchonizer;
		this.stopFlag = stopFlag;
		this.nWorkers = nWorkers;
		this.debugLogs = debugLogs;
		this.listeners = new ArrayList<>();
		this.workers = new HashSet<>();

		bag = new TaskBag();
		taskLatch = new TaskCompletionLatch(nWorkers);
		log( "creating workers...");

		for (int i = 0; i < nWorkers - 1; i++) {
			WorkerAgent worker = new WorkerAgent(bag, taskLatch, stopFlag, debugLogs);
			workers.add(worker);
		}
		WorkerAgent worker = new WorkerAgent(bag, taskLatch, stopFlag, debugLogs);
		workers.add(worker);

		int nBodies = model.getNBodies();
		List<Body> bodies = model.getBodies();
		bodiesToTask = new ArrayList<>();
		int chunkSize = nBodies / nWorkers;

		for(int i = 0; i < nWorkers - 1; i++) {
			bodiesToTask.add(bodies.subList(i * chunkSize, (i + 1) * chunkSize));
			log(String.valueOf(bodiesToTask.get(i).size()));
		}
		bodiesToTask.add(bodies.subList((nWorkers - 1) * chunkSize, bodies.size()));
		log(String.valueOf(bodiesToTask.get(nWorkers - 1).size()));
	}
	
	public void run() {
		for(WorkerAgent worker: workers) {
			worker.start();
		}

		log( "waiting for start...");
		synchonizer.waitStart();
   		boolean maxStepsReached = false;
		long tStart = System.currentTimeMillis();

		while (!maxStepsReached && stopFlag.isNotSet()) {
			try {
	   			long tIterStart = System.currentTimeMillis();
				notifyStateChange("Processing...");
				log("allocating tasks...");

	       		taskLatch.reset();
		       	bag.clear();

				TaskAssignmentStrategy strategy = (b, st) -> {
					for (int i = 0; i < nWorkers - 1; i++){
						Task t = new Task(b.get(i), st);
						bag.addNewTask(t);
					}
					Task t = new Task(b.get(b.size() - 1), st);
					bag.addNewTask(t);
				};

				strategy.distribute(bodiesToTask, model::updateBodyPosition);

	       		log("wait completion of first round");
	       		taskLatch.waitCompletion();
				log("first round completed");

				taskLatch.reset();
				bag.clear();

				strategy.distribute(bodiesToTask, model::updateBodyVelocity);

				log("wait completion of second round");
				taskLatch.waitCompletion();
				log("second round completed");
				log("update virtual time");

				/* update the model */
				maxStepsReached = model.advanceVirtualTime();
	       		/* update the view */
	       		if (stopFlag.isNotSet()) {
					notifyUpdate();
					long tIterEnd = System.currentTimeMillis();
					long tIterElapsed = tIterEnd - tIterStart;
					log("iter: " + model.getNIter() + " time elapsed: " + tIterElapsed + " ms");
					waitForNextFrame(tIterStart);
	      		} else {
	      			log("interrupted");
	      		}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		long tEnd = System.currentTimeMillis();
		long tElapsed = tEnd - tStart;
		System.out.println(" time: " + tElapsed);
		if (stopFlag.isNotSet()) {
			notifyStateChange("Completed");
		} else {
			notifyStateChange("Interrupted");
		}
	}
	
	private void log(String msg){
		if(debugLogs) {
			synchronized(System.out){
				System.out.println("[ master ] " + msg);
			}
		}
	}

	private void waitForNextFrame(final long current) {
		final long dt = System.currentTimeMillis() - current;
		if (dt < MS_BETWEEN_FRAMES) {
			try {
				Thread.sleep(MS_BETWEEN_FRAMES - dt);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void addListener(SimView view) {
		listeners.add(view);
	}

	@Override
	public void notifyUpdate() {
		if(!listeners.isEmpty()) {
			List<Body> dCopy = new ArrayList<>();
			for(Body b: model.getBodies()) {
				dCopy.add(new Body(b));
			}
			for (SimView v: listeners){
				v.update(dCopy,
						model.getVirtualTime(),
						model.getNIter(),
						model.getBoundary()
				);
			}
		}
	}

	@Override
	public void notifyStateChange(String stateDescription) {
		for (SimView v: listeners){
			v.changeState(stateDescription);
		}
	}
}
