package com.ps.weatherapp.models;

public class Wind {
    private double speed;

    public Wind(double speed, int deg, double gust) {
        this.speed = speed;
    }

    public Wind() {}

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}