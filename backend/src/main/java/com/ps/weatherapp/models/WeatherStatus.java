package com.ps.weatherapp.models;

public class WeatherStatus {
    private String status;
    private String description;

    public WeatherStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public WeatherStatus() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
