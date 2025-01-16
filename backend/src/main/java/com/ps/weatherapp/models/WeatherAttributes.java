package com.ps.weatherapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class WeatherAttributes {
    private double temp;

    @JsonProperty("feels_like")
    private double feelsLike;

    public WeatherAttributes(double temp, double feelsLike) {
        this.temp = temp;
        this.feelsLike = feelsLike;
    }

    public WeatherAttributes() {}

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

}