package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private final RobotModel robotModel;

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    private volatile double m_robotDirection = 0;
    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.005;

    public GameVisualizer(RobotModel robotModel) {
        this.robotModel = robotModel;
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    protected void setTargetPosition(Point p) {
        robotModel.setTargetPosition(p.x, p.y);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void onModelUpdateEvent() {
        double distance = distance(robotModel.getTargetPositionX(), robotModel.getTargetPositionY(),
                robotModel.getPositionX(), robotModel.getPositionY());
        if (distance < 0.5) {
            return;
        }

        double angleToTarget = angleTo(robotModel.getPositionX(), robotModel.getPositionY(),
                robotModel.getTargetPositionX(), robotModel.getTargetPositionY());
        double angleDifference = asNormalizedRadians(angleToTarget - m_robotDirection);

        double angularVelocity = 0;
        if (angleDifference > Math.PI) {
            angleDifference = angleDifference - 2 * Math.PI;
        }

        angularVelocity = angleDifference * 0.5; // Плавный поворот

        double velocity = maxVelocity;

        if (Math.abs(angleDifference) > Math.PI/4) {
            velocity *= 0.5;
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX = robotModel.getPositionX() + velocity * Math.cos(m_robotDirection) * duration;
        double newY = robotModel.getPositionY() + velocity * Math.sin(m_robotDirection) * duration;

        boolean hitBoundary = false;
        if (newX < 0 || newX > getWidth() || newY < 0 || newY > getHeight()) {
            hitBoundary = true;
        }

        if (hitBoundary) {
            angularVelocity = maxAngularVelocity * 2 * (Math.random() > 0.5 ? 1 : -1);
            velocity *= 0.3; //замедляемся у границы

            newX = applyLimits(newX, 10, getWidth() - 10); //чтобы не застревать на границе
            newY = applyLimits(newY, 10, getHeight() - 10);
        }

        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration); // обновляем направление

        robotModel.setPosition(newX, newY);
        m_robotDirection = newDirection;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(robotModel.getPositionX()), round(robotModel.getPositionY()), m_robotDirection);
        drawTarget(g2d, round(robotModel.getTargetPositionX()), round(robotModel.getTargetPositionY()));
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
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
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.RED);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}