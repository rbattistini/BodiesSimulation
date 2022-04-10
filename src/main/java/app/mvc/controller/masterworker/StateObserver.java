package app.mvc.controller.masterworker;

import app.mvc.view.SimView;

public interface StateObserver {
    void addListener(SimView view);
    void notifyUpdate();
    void notifyStateChange(String stateDescription);
}
