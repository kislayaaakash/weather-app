package com.ps.weatherapp.models;

import java.util.List;

public class WeatherPrediction {

    private double temperature;
    private List<WeatherStatus> weather;
    private String advice;
    private String time;

    public WeatherPrediction() {
    }

    public WeatherPrediction(double temperature, List<WeatherStatus> weather, String advice, String time) {
        this.temperature = temperature;
        this.weather = weather;
        this.advice = advice;
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public List<WeatherStatus> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherStatus> weather) {
        this.weather = weather;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String description) {
        this.advice = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
