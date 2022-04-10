package app.mvc.view.swing;

import app.mvc.controller.SimController;
import app.util.Body;
import app.util.Boundary;
import java.util.List;

public interface Frame {
    void updateText(final String s);
    void addListener(SimController controller);
    void notifyStarted();
    void notifyStopped();
    void updateImage(List<Body> bodies, double vt, long iter, Boundary bounds);
    void display();
}
