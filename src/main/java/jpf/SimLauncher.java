package jpf;

import gov.nasa.jpf.vm.Verify;
import app.mvc.controller.Flag;
import app.mvc.controller.SimController;
import app.mvc.controller.SimulationController;
import app.mvc.controller.StartSynchonizer;
import app.mvc.model.SimModel;
import app.mvc.model.SimulationModel;

public class SimLauncher {

    private static final double BOUNDARY_WIDTH = 20;
    private static final double BOUNDARY_HEIGHT = 20;

    public static void main(String... args) throws InterruptedException {
        Verify.beginAtomic();
        int nSteps = 2;
        int nBodies = 2;
        int nWorkers = 2;

        SimModel model = new SimulationModel(BOUNDARY_WIDTH, BOUNDARY_HEIGHT, nSteps, nBodies);
        StartSynchonizer synchronizer = new StartSynchonizer();
        Flag stopFlag = new Flag();
        SimController controller = new SimulationController(synchronizer, stopFlag);
        MasterAgent ma = new MasterAgent(model, synchronizer, stopFlag, nWorkers);
        Verify.endAtomic();

        ma.start();

        Thread.sleep(1000);
        controller.started();
        ma.join();
        System.exit(0);
    }
}
