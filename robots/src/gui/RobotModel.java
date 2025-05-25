package gui;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class RobotModel extends Observable {
    private volatile double positionX;
    private volatile double positionY;
    private volatile double targetPositionX = 100;
    private volatile double targetPositionY = 100;
    private volatile double direction;
    private volatile double velocity = 0;

    private static final double maxVelocity = 0.3;
    private static final double maxAngularVelocity = 0.05;
    private static final double stopDistance = 0.5;
    private final Timer timer;
    private int fieldWidth = Integer.MAX_VALUE;
    private int fieldHeight = Integer.MAX_VALUE;

    public RobotModel(double initialX, double initialY) {
        this.positionX = initialX;
        this.positionY = initialY;
        this.timer = new Timer("Robot timer", true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updatePosition();
            }
        }, 0, 10);
    }

    public void setFieldSize(int width, int height) {
        this.fieldWidth = width;
        this.fieldHeight = height;
    }

    public double getDirection() {
        return direction;
    }

    public void setTargetPosition(double x, double y) {
        this.targetPositionX = Math.max(5, Math.min(fieldWidth - 5, x));
        this.targetPositionY = Math.max(5, Math.min(fieldHeight - 5, y));
        notifyObservers();
    }

    public void updatePosition() {
        double distance = distance(targetPositionX, targetPositionY, positionX, positionY);
        if (distance < stopDistance) {
            if (velocity != 0) {
                velocity = 0;
                notifyObservers();
            }
            return;
        }

        double angleToTarget = angleTo(positionX, positionY, targetPositionX, targetPositionY);
        double angleDifference = normalizeAngle(angleToTarget - direction);

        double angleThreshold = 0.1 + 0.4 * (distance / 100);
        angleThreshold = Math.min(angleThreshold, 0.3);

        double angularVelocity = 0;
        if (Math.abs(angleDifference) > angleThreshold) {
            angularVelocity = maxAngularVelocity * Math.signum(angleDifference);

            if (Math.abs(angleDifference) > Math.PI/2) {
                angularVelocity *= 1.5;
            }
        }

        double targetVelocity = maxVelocity *
                (1 - 0.8 * Math.abs(angleDifference)/Math.PI) *
                Math.min(1, distance / 50);

        if (targetVelocity > velocity) {
            velocity = Math.min(targetVelocity, velocity + 0.005);
        } else {
            velocity = Math.max(targetVelocity, velocity - 0.01);
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {

        direction = normalizeAngle(direction + angularVelocity * duration);
        double newX = positionX + velocity * Math.cos(direction) * duration;
        double newY = positionY + velocity * Math.sin(direction) * duration;

        double margin = 15;
        newX = Math.max(margin, Math.min(fieldWidth - margin, newX));
        newY = Math.max(margin, Math.min(fieldHeight - margin, newY));

        if (newX <= margin || newX >= fieldWidth - margin) {
            direction = normalizeAngle(Math.PI - direction);
            newX = positionX;
        }
        if (newY <= margin || newY >= fieldHeight - margin) {
            direction = normalizeAngle(-direction);
            newY = positionY;
        }

        positionX = newX;
        positionY = newY;
        notifyObservers();
    }

    public void notifyObservers() {
        setChanged();
        notifyObservers(new double[]{positionX, positionY, targetPositionX, targetPositionY});
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx*dx + dy*dy);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        return normalizeAngle(Math.atan2(toY - fromY, toX - fromX));
    }

    private static double normalizeAngle(double angle) {
        while (angle < -Math.PI) angle += 2*Math.PI;
        while (angle > Math.PI) angle -= 2*Math.PI;
        return angle;
    }
}