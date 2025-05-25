package gui;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.util.prefs.Preferences;
import java.awt.event.MouseEvent;

public class GameWindow extends JInternalFrame implements WindowState {
    private final GameVisualizer m_visualizer;
    private final RobotModel robotModel;
    private final Preferences prefs = Preferences.userNodeForPackage(GameWindow.class);

    public GameWindow(RobotModel robotModel) {
        super("Игровое поле", true, true, true, true);
        this.robotModel = robotModel;

        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);

        getContentPane().add(panel);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        pack();

        robotModel.addObserver(m_visualizer);

        m_visualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                robotModel.setTargetPosition(e.getPoint().x, e.getPoint().y);
            }
        });

        m_visualizer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                robotModel.setFieldSize(m_visualizer.getWidth(), m_visualizer.getHeight());
            }
        });
        load();
        robotModel.setFieldSize(m_visualizer.getWidth(), m_visualizer.getHeight());
    }

    @Override
    public void save() {
        Rectangle bounds = getBounds();
        prefs.putInt("GameWindow_X", bounds.x);
        prefs.putInt("GameWindow_Y", bounds.y);
        prefs.putInt("GameWindow_WIDTH", bounds.width);
        prefs.putInt("GameWindow_HEIGHT", bounds.height);
    }

    @Override
    public void load() {
        int x = prefs.getInt("GameWindow_X", 10);
        int y = prefs.getInt("GameWindow_Y", 10);
        int width = prefs.getInt("GameWindow_WIDTH", 400);
        int height = prefs.getInt("GameWindow_HEIGHT", 400);
        setBounds(x, y, width, height);
    }
}