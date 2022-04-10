package app.mvc.view.swing;

import app.mvc.controller.SimController;
import app.util.Body;
import app.util.Boundary;
import app.mvc.view.swing.Frame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;

public class VisualiserFrame extends JFrame implements ActionListener, Frame {

    private final static String TITLE = "Bodies Simulation Viewer";
    private final VisualiserPanel panel;
    private final JTextField state;
    private final List<SimController> listeners;

    public VisualiserFrame(int w, int h){
        super(TITLE);
        setSize(w,h);
        setResizable(false);
        listeners = new ArrayList<>();

        JButton startButton = new JButton("start");
        JButton stopButton = new JButton("stop");
        JPanel controlPanel = new JPanel();
        startButton.setFocusable(false);
        stopButton.setFocusable(false);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        panel = new VisualiserPanel(w,h);

        JPanel infoPanel = new JPanel();
        state = new JTextField(20);
        state.setText("Idle");
        state.setEditable(false);
        infoPanel.add(new JLabel("State"));
        infoPanel.add(state);
        JPanel cp = new JPanel();
        LayoutManager layout = new BorderLayout();
        cp.setLayout(layout);
        cp.add(BorderLayout.NORTH,controlPanel);
        cp.add(BorderLayout.CENTER, panel);
        cp.add(BorderLayout.SOUTH, infoPanel);
        setContentPane(cp);

        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void updateText(String s) {
        SwingUtilities.invokeLater(() -> state.setText(s));
    }

    @Override
    public void addListener(SimController controller) {
        listeners.add(controller);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        String cmd = ev.getActionCommand();
        if (cmd.equals("start")){
            notifyStarted();
        } else if (cmd.equals("stop")){
            notifyStopped();
        }
    }

    @Override
    public void notifyStarted() {
        for (SimController l: listeners){
            l.started();
        }
    }

    @Override
    public void notifyStopped() {
        for (SimController l: listeners){
            l.stopped();
        }
    }

    @Override
    public void updateImage(List<Body> bodies, double vt, long iter, Boundary bounds){
        try {
            SwingUtilities.invokeAndWait(() -> {
                panel.updateImage(bodies, vt, iter, bounds);
                repaint();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display(){
        try {
            SwingUtilities.invokeAndWait(() -> this.setVisible(true));
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}