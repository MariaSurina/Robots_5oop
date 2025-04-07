package gui;

import java.util.Observable;

public class RobotModel extends Observable {
    private volatile double positionX;
    private volatile double positionY;
    private volatile double targetPositionX;
    private volatile double targetPositionY;

    public RobotModel(double initialX, double initialY) {
        this.positionX = initialX;
        this.positionY = initialY;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getTargetPositionX() {
        return targetPositionX;
    }

    public double getTargetPositionY() {
        return targetPositionY;
    }

    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
        setChanged();
        notifyObservers();
    }

    public void setTargetPosition(double x, double y) {
        this.targetPositionX = x;
        this.targetPositionY = y;
        setChanged();
        notifyObservers();
    }
}