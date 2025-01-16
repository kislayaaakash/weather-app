package com.ps.weatherapp.models;

import java.util.List;
import java.util.Map;

public class CityWeatherAdvice {

    private String message;
    private int status;
    private Map<String, List<WeatherPrediction>> data;

    public CityWeatherAdvice(String message, Map<String, List<WeatherPrediction>> data, int status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public CityWeatherAdvice() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String errorMessage) {
        this.message = errorMessage;
    }

    public Map<String, List<WeatherPrediction>> getData() {
        return data;
    }

    public void setData(Map<String, List<WeatherPrediction>> data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
