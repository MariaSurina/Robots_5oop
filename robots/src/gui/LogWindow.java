package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.util.prefs.Preferences;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener, WindowState {
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
    private final Preferences prefs = Preferences.userNodeForPackage(LogWindow.class);

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
        setName("LogWindow");
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        load();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void save() {
        Rectangle bounds = getBounds();
        prefs.putInt("LogWindow_X", bounds.x);
        prefs.putInt("LogWindow_Y", bounds.y);
        prefs.putInt("LogWindow_WIDTH", bounds.width);
        prefs.putInt("LogWindow_HEIGHT", bounds.height);
    }

    @Override
    public void load() {
        int x = prefs.getInt("LogWindow_X", 10);
        int y = prefs.getInt("LogWindow_Y", 10);
        int width = prefs.getInt("LogWindow_WIDTH", 300);
        int height = prefs.getInt("LogWindow_HEIGHT", 800);
        setBounds(x, y, width, height);
    }
}
