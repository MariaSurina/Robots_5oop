package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

public class CoordinatesWindow extends JInternalFrame implements Observer, WindowState {
    private JLabel coordinatesLabel;
    private final Preferences prefs = Preferences.userNodeForPackage(CoordinatesWindow.class);

    public CoordinatesWindow() {
        super("Координаты", true, true, true, true);
        setSize(300, 100);
        coordinatesLabel = new JLabel("<html>Червяк: (0.0, 0.0) <br> Точка: (0.0, 0.0)</html>");
        add(coordinatesLabel, BorderLayout.CENTER);
    }

    @Override
    public void update(Observable notif, Object arg) {
        if (notif instanceof RobotModel && arg instanceof double[]) {
            double[] coordinates = (double[]) arg;
            coordinatesLabel.setText(String.format(
                    "<html>Червяк: (%.1f, %.1f) <br> Точка: (%.1f, %.1f)</html>",
                    coordinates[0], coordinates[1], coordinates[2], coordinates[3]
            ));
        }
    }

    @Override
    public void save() {
        Rectangle bounds = getBounds();
        prefs.putInt("CoordinatesWindow_X", bounds.x);
        prefs.putInt("CoordinatesWindow_Y", bounds.y);
        prefs.putInt("CoordinatesWindow_WIDTH", bounds.width);
        prefs.putInt("CoordinatesWindow_HEIGHT", bounds.height);
    }

    @Override
    public void load() {
        int x = prefs.getInt("CoordinatesWindow_X", 10);
        int y = prefs.getInt("CoordinatesWindow_Y", 10);
        int width = prefs.getInt("CoordinatesWindow_WIDTH", 300);
        int height = prefs.getInt("CoordinatesWindow_HEIGHT", 100);
        setBounds(x, y, width, height);
    }
}