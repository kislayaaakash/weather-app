package com.ps.weatherapp.exceptions;

public class CityWeatherException extends RuntimeException {
    private final String status;
    private final String message;

    public CityWeatherException(String status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
