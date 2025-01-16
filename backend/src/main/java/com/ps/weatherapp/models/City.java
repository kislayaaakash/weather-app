package com.ps.weatherapp.models;

public class City {
    private String name;
    private int timezone;

    public City(String name, int timezone) {
        this.name = name;
        this.timezone = timezone;
    }
    public City() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }
}