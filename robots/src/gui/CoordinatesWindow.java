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
        coordinatesLabel = new JLabel("<html>Робот: (0.0, 0.0) <br> Точка: (0.0, 0.0)</html>");
        add(coordinatesLabel, BorderLayout.CENTER);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof RobotModel) {
            RobotModel model = (RobotModel) o;
            StringBuilder sb = new StringBuilder();
            sb.append("<html>Робот: (")
                    .append(String.format("%.1f", model.getPositionX()))
                    .append(", ")
                    .append(String.format("%.1f", model.getPositionY()))
                    .append(") <br>Точка: (")
                    .append(String.format("%.1f", model.getTargetPositionX()))
                    .append(", ")
                    .append(String.format("%.1f", model.getTargetPositionY()))
                    .append(")</html>");
            coordinatesLabel.setText(sb.toString());
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
