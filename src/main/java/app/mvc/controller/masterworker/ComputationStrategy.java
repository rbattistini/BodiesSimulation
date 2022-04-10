package app.mvc.controller.masterworker;

import app.util.Body;

@FunctionalInterface
public interface ComputationStrategy {
    void compute(Body b);
}
