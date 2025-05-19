package gui;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class RobotModel extends Observable {
    private volatile double positionX;
    private volatile double positionY;
    private volatile double targetPositionX;
    private volatile double targetPositionY;
    private volatile double direction;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.005;
    private final Timer timer;

    public RobotModel(double initialX, double initialY) {
        this.positionX = initialX;
        this.positionY = initialY;
        this.direction = 0;
        this.timer = new Timer("robot timer", true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updatePosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
        }, 0, 10);
    }

    public double getDirection() {
        return direction;
    }

    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
        setChanged();
        notifyObservers(new double[]{x, y, targetPositionX, targetPositionY});
    }

    public void setTargetPosition(double x, double y) {
        this.targetPositionX = x;
        this.targetPositionY = y;
        setChanged();
        notifyObservers(new double[]{positionX, positionY, x, y});
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

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    public void updatePosition(int width, int height) {
        double distance = distance(targetPositionX, targetPositionY, positionX, positionY);
        if (distance < 0.5) {
            return;
        }

        double angleToTarget = angleTo(positionX, positionY, targetPositionX, targetPositionY);
        double angleDifference = asNormalizedRadians(angleToTarget - direction);

        double angularVelocity = 0;
        if (angleDifference > Math.PI) {
            angleDifference = angleDifference - 2 * Math.PI;
        }

        angularVelocity = angleDifference * 0.5;

        double velocity = maxVelocity;
        if (Math.abs(angleDifference) > Math.PI/4) {
            velocity *= 0.5;
        }

        moveRobot(velocity, angularVelocity, 10, width, height);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration, int width, int height) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX = positionX + velocity * Math.cos(direction) * duration;
        double newY = positionY + velocity * Math.sin(direction) * duration;

        boolean hitBoundary = false;
        if (newX < 0 || newX > width || newY < 0 || newY > height) {
            hitBoundary = true;
        }

        if (hitBoundary) {
            angularVelocity = maxAngularVelocity * 2 * (Math.random() > 0.5 ? 1 : -1);
            velocity *= 0.3;

            newX = applyLimits(newX, 10, width - 10);
            newY = applyLimits(newY, 10, height - 10);
        }

        double newDirection = asNormalizedRadians(direction + angularVelocity * duration);
        direction = newDirection;

        setPosition(newX, newY);
    }
}