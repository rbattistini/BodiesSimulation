package app.mvc.model;

import app.util.*;

import java.util.ArrayList;
import java.util.Random;

public class SimulationModel implements SimModel {

    private final ArrayList<Body> bodies;
    private final Boundary bounds;
    private final long nSteps;
    private final int nBodies;
    private double virtualTime;
    private final double timeStep;
    private long iter;

    public SimulationModel(double boundaryWidth, double boundaryHeight, int nSteps, int nBodies) {
        this.bounds = new Boundary(-boundaryWidth, -boundaryHeight, boundaryWidth, boundaryHeight);
        this.bodies = new ArrayList<>();
        this.nSteps = nSteps;
        this.nBodies = nBodies;
        this.virtualTime = 0;
        this.timeStep = 0.001;
        this.iter = 0;

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < nBodies; i++) {
            double x = bounds.getX0()*0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
            double y = bounds.getY0()*0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
            Body b = new Body(i, new P2d(x, y), new V2d(0, 0), 10);
            bodies.add(b);
        }
    }

    @Override
    public void updateBodyVelocity(Body b) {
        V2d totalForce = computeTotalForceOnBody(b);
        V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass()); // acceleration
        b.updateVelocity(acc, timeStep);
    }

    @Override
    public void updateBodyPosition(Body b) {
        b.updatePos(timeStep);
        b.checkAndSolveBoundaryCollision(bounds);
    }

    @Override
    public V2d computeTotalForceOnBody(Body b) {

        V2d totalForce = new V2d(0, 0);

        /* compute total repulsive force */
        for (Body otherBody : bodies) {
            if (!b.equals(otherBody)) {
                V2d forceByOtherBody = null;
                try {
                    forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                } catch (InfiniteForceException e) {
                    e.printStackTrace();
                }
                assert forceByOtherBody != null;
                totalForce.sum(forceByOtherBody);
            }
        }

        /* add friction force */
        totalForce.sum(b.getCurrentFrictionForce());

        return totalForce;
    }

    @Override
    public boolean advanceVirtualTime() {
        if(iter < nSteps) {
            virtualTime = virtualTime + timeStep;
            iter++;
            return false;
        } else {
            return true;
        }
    }

    @Override
    public double getVirtualTime() {
        return virtualTime;
    }

    @Override
    public int getNBodies() {
        return nBodies;
    }

    @Override
    public Boundary getBoundary() {
        return bounds;
    }

    @Override
    public ArrayList<Body> getBodies() {
        return bodies;
    }

    @Override
    public long getNIter() {
        return iter;
    }
}
