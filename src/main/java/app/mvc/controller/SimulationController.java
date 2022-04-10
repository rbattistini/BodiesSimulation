package app.mvc.controller;

/**
 * Controller part of the application - passive part.
 */
public class SimulationController implements SimController {

	private final StartSynchonizer synchonizer;
	private final Flag stopFlag;
	
	public SimulationController(StartSynchonizer synchonizer, Flag stopFlag){
		this.synchonizer = synchonizer;
		this.stopFlag = stopFlag;
	}

	@Override
	public synchronized void started() {
		stopFlag.reset();
		synchonizer.notifyStarted();
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

}
