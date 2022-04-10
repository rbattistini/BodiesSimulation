package app.mvc.view;

import app.mvc.controller.SimController;
import app.mvc.view.swing.VisualiserFrame;
import app.util.Body;
import app.util.Boundary;
import java.util.List;

public class SimulationView implements SimView {
        
	private VisualiserFrame frame;
	private final int w;
	private final int h;

	/**
     * Creates a view of the specified size (in pixels)
     */
    public SimulationView(int w, int h){
		this.w = w;
		this.h = h;
	}

	@Override
	public void createGUI() {
		frame = new VisualiserFrame(w, h);
	}

	@Override
	public void addListener(SimController controller) {
		frame.addListener(controller);
	}

	@Override
    public void update(List<Body> bodies, double vt, long iter, Boundary bounds){
 	   frame.updateImage(bodies, vt, iter, bounds);
    }

	@Override
	public void display() {
		frame.display();
	}

	@Override
	public void changeState(final String s) {
		frame.updateText(s);
	}
}
