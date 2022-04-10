package app.mvc.controller.masterworker;

import app.util.Body;
import java.util.List;

@FunctionalInterface
public interface TaskAssignmentStrategy {
    void distribute(List<List<Body>> l, ComputationStrategy strategy);
}
