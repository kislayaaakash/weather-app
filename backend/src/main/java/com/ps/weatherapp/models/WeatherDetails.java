package com.ps.weatherapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


public class WeatherDetails {
    private long dt;
    private WeatherAttributes main;
    private List<WeatherInfo> weather;
    private Wind wind;

    @JsonProperty("dt_txt")
    private String dtTxt;

    public WeatherDetails(long dt, WeatherAttributes main, List<WeatherInfo> weather, Wind wind, String dtTxt) {
        this.dt = dt;
        this.main = main;
        this.weather = weather;
        this.wind = wind;
        this.dtTxt = dtTxt;
    }

    public WeatherDetails() {}

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public WeatherAttributes getMain() {
        return main;
    }

    public void setMain(WeatherAttributes main) {
        this.main = main;
    }

    public List<WeatherInfo> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherInfo> weather) {
        this.weather = weather;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public void setDtTxt(String dtTxt) {
        this.dtTxt = dtTxt;
    }
}
