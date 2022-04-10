package app.mvc.controller.task;

import java.util.LinkedList;

public class TaskBag {

	private final LinkedList<Task> buffer;

	public TaskBag() {
		buffer = new LinkedList<>();
	}

	public synchronized void clear() {
		buffer.clear();
	}
	
	public synchronized void addNewTask(Task task) {
		buffer.addLast(task);
		notifyAll();
	}

	public synchronized Task getATask() {
		while (buffer.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return buffer.removeFirst(); 
	}
}
