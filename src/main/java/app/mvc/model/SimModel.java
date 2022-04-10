package app.mvc.model;

import app.util.Body;
import app.util.Boundary;
import app.util.V2d;

import java.util.ArrayList;

public interface SimModel {
    void updateBodyVelocity(Body b);
    void updateBodyPosition(Body b);
    V2d computeTotalForceOnBody(Body b);
    int getNBodies();
    boolean advanceVirtualTime();
    double getVirtualTime();
    Boundary getBoundary();
    ArrayList<Body> getBodies();
    long getNIter();
}
