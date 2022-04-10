package app;

import com.beust.jcommander.JCommander;
import app.mvc.view.SimulationView;
import app.mvc.controller.Flag;
import app.mvc.controller.SimController;
import app.mvc.controller.SimulationController;
import app.mvc.controller.StartSynchonizer;
import app.mvc.controller.masterworker.MasterAgent;
import app.mvc.model.SimModel;
import app.mvc.model.SimulationModel;
import com.beust.jcommander.Parameter;

public class SimLauncher {

    @FunctionalInterface
    interface InRangeChecker {
        void check(int arg, int min, int max);
    }

    private static final int WIDTH = 620;
    private static final int HEIGHT = 620;
    private static final double BOUNDARY_WIDTH = 20;
    private static final double BOUNDARY_HEIGHT = 20;
    private static final int MIN_STEPS = 0;
    private static final int MAX_STEPS = 100_000;
    private static final int MIN_NBODIES = 1;
    private static final int MAX_NBODIES = 5_000;
    private static final int MIN_NWORKERS = 1;
    private static final int MAX_NWORKERS = Runtime.getRuntime().availableProcessors();

    public static class Args {
        @Parameter(names = "-nSteps", description = "Steps of the simulation")
        private int nSteps;

        @Parameter(names = "-nBodies", description = "Number of bodies")
        private int nBodies;

        @Parameter(names = "-nWorkers", description = "Number of threads to use")
        private int nWorkers;

        @Parameter(names = "-guiEnabled", description = "GUI or command line app")
        private boolean guiEnabled = false;

        @Parameter(names = "-debug", description = "Debug mode")
        private boolean debugLogs = false;
    }

    public static void main(String... args) throws InterruptedException {
        Args arguments = new Args();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        int nSteps = arguments.nSteps;
        int nBodies = arguments.nBodies;
        int nWorkers = arguments.nWorkers;
        boolean debugLogs = arguments.debugLogs;
        boolean guiEnabled = arguments.guiEnabled;

        InRangeChecker pChecker = (value, min, max) -> {
            if (value < min || value > max) {
                System.err.println(value + " must be between " + min + " and " + max);
                System.err.println("Current is: " + value);
                System.exit(0);
            }
        };

        pChecker.check(nSteps, MIN_STEPS, MAX_STEPS);
        pChecker.check(nBodies, MIN_NBODIES, MAX_NBODIES);
        pChecker.check(nWorkers, MIN_NWORKERS, MAX_NWORKERS);

        if (nWorkers > nBodies) {
            System.err.println("The number of workers must be less than or equal the number of bodies");
            System.exit(0);
        }

        SimModel model = new SimulationModel(BOUNDARY_WIDTH, BOUNDARY_HEIGHT, nSteps, nBodies);
        SimulationView view = new SimulationView(WIDTH, HEIGHT);

        StartSynchonizer synchronizer = new StartSynchonizer();
        Flag stopFlag = new Flag();
        SimController controller = new SimulationController(synchronizer, stopFlag);
        MasterAgent ma = new MasterAgent(model, synchronizer, stopFlag, nWorkers, debugLogs);

        ma.start();

        if (guiEnabled) {
            ma.addListener(view);
            view.createGUI();
            view.display();
            view.addListener(controller);
        } else {
            Thread.sleep(1000);
            controller.started();
            ma.join();
            System.exit(0);
        }
    }
}
