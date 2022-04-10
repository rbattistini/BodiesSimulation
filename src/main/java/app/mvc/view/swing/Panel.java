package app.mvc.view.swing;

import app.util.Body;
import app.util.Boundary;
import java.util.List;

public interface Panel {
    void updateImage(List<Body> bodies, double vt, long iter, Boundary bounds);
}
