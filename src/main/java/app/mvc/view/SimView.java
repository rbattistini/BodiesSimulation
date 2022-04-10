package app.mvc.view;

import app.mvc.controller.SimController;
import app.util.Body;
import app.util.Boundary;
import java.util.List;

public interface SimView {
    void createGUI();
    void addListener(SimController controller);
    void update(List<Body> bodies, double vt, long iter, Boundary bounds);
    void display();
    void changeState(final String s);
}
