package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final MenuWindow menuWindow;
    private boolean isClosing = false;

    private LogWindow logWindow;
    private GameWindow gameWindow;
    private CoordinatesWindow coordinatesWindow;
    private RobotModel robotModel;

    public MainApplicationFrame() {
        robotModel = new RobotModel(100, 100);
        setContentPane(desktopPane);

        logWindow = createLogWindow();
        gameWindow = new GameWindow(robotModel);
        coordinatesWindow = new CoordinatesWindow();
        robotModel.addObserver(coordinatesWindow);

        menuWindow = new MenuWindow(this);
        setJMenuBar(menuWindow.generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        addWindow(logWindow);
        addWindow(gameWindow);
        addWindow(coordinatesWindow);
        loadWindowStates();
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void saveWindowStates() {
        logWindow.save();
        gameWindow.save();
        coordinatesWindow.save();
    }

    private void loadWindowStates() {
        logWindow.load();
        gameWindow.load();
        coordinatesWindow.load();
    }

    public void handleExit() {
        if (!isClosing) {
            isClosing = true;
            saveWindowStates();
            menuWindow.confirmExit();
        }
    }

    public void resetClosingFlag() {
        isClosing = false;
    }
}