package jpf;

import gov.nasa.jpf.vm.Verify;
import app.mvc.controller.Flag;
import app.mvc.controller.StartSynchonizer;
import app.mvc.controller.masterworker.TaskAssignmentStrategy;
import app.mvc.controller.task.Task;
import app.mvc.controller.task.TaskBag;
import app.mvc.controller.task.TaskCompletionLatch;
import app.mvc.model.SimModel;
import app.util.Body;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MasterAgent extends Thread {

	private final SimModel model;
	private final Flag stopFlag;
	private final StartSynchonizer synchonizer;
	private final int nWorkers;
	private final TaskBag bag;
	private final TaskCompletionLatch taskLatch;
	private final List<List<Body>> bodiesToTask;
	private final Set<WorkerAgent> workers;

	public MasterAgent(SimModel model, StartSynchonizer synchonizer, Flag stopFlag, int nWorkers){
		this.model = model;
		this.synchonizer = synchonizer;
		this.stopFlag = stopFlag;
		this.nWorkers = nWorkers;
		this.workers = new HashSet<>();

		bag = new TaskBag();
		taskLatch = new TaskCompletionLatch(nWorkers);

		for (int i = 0; i < nWorkers - 1; i++) {
			WorkerAgent worker = new WorkerAgent(bag, taskLatch, stopFlag);
			workers.add(worker);
		}
		WorkerAgent worker = new WorkerAgent(bag, taskLatch, stopFlag);
		workers.add(worker);

		int nBodies = model.getNBodies();
		List<Body> bodies = model.getBodies();
		bodiesToTask = new ArrayList<>();
		int chunkSize = nBodies / nWorkers;

		for(int i = 0; i < nWorkers - 1; i++) {
			bodiesToTask.add(bodies.subList(i * chunkSize, (i + 1) * chunkSize));
		}
		bodiesToTask.add(bodies.subList((nWorkers - 1) * chunkSize, bodies.size()));
	}
	
	public void run() {
		for(WorkerAgent worker: workers) {
			worker.start();
		}

		synchonizer.waitStart();
   		boolean maxStepsReached = false;

		while (!maxStepsReached && stopFlag.isNotSet()) {
			try {
				Verify.beginAtomic();
	       		taskLatch.reset();
				Verify.endAtomic();
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

	       		taskLatch.waitCompletion();

				Verify.beginAtomic();
				taskLatch.reset();
				Verify.endAtomic();
				bag.clear();

				strategy.distribute(bodiesToTask, model::updateBodyVelocity);

				taskLatch.waitCompletion();

				/* update the model */
				maxStepsReached = model.advanceVirtualTime();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
