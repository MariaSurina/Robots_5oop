package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements Observer {
    private double robotPositionX;
    private double robotPositionY;
    private double targetPositionX;
    private double targetPositionY;
    private double direction;

    private final Timer m_timer = initTimer();

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameVisualizer() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
            }
        });

        setDoubleBuffered(true);
        setBackground(Color.WHITE);
    }

    protected void setTargetPosition(Point p) {
        repaint();
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static int round(double value) {
        return (int)(value + 0.5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform originalTransform = g2d.getTransform();

        if (targetPositionX != 0 || targetPositionY != 0) {
            drawTarget(g2d, round(targetPositionX), round(targetPositionY));
        }

        drawRobot(g2d, round(robotPositionX), round(robotPositionY), direction);
        g2d.setTransform(originalTransform);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);

        g.setColor(Color.BLUE);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.RED);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof RobotModel && arg instanceof double[]) {
            double[] coords = (double[]) arg;
            robotPositionX = coords[0];
            robotPositionY = coords[1];
            targetPositionX = coords[2];
            targetPositionY = coords[3];
            direction = ((RobotModel) o).getDirection();
            repaint();
        }
    }
}