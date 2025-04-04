package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final MenuWindow menuWindow;

    private LogWindow logWindow;
    private GameWindow gameWindow;

    public MainApplicationFrame() {
        setContentPane(desktopPane);

        // Создание внутренних окон
        logWindow = createLogWindow();
        gameWindow = new GameWindow();

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
    }

    public void handleExit() {
        saveWindowStates();
        menuWindow.confirmExit();
    }
}
