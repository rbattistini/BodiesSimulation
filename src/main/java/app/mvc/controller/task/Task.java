package app.mvc.controller.task;

import app.mvc.controller.masterworker.ComputationStrategy;
import app.util.Body;

import java.util.List;

public class Task {

	private List<Body> bodies;
	private ComputationStrategy strategy;

	public Task() {
	}

	public Task(List<Body> bodies, ComputationStrategy strategy) {
		this.bodies = bodies;
		this.strategy = strategy;
	}

	public List<Body> getBodies() {
		return bodies;
	}

	public ComputationStrategy getStrategy() {
		return strategy;
	}
}
