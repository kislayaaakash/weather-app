package com.ps.weatherapp.utilities;

import com.ps.weatherapp.models.WeatherInfo;

import java.util.HashMap;
import java.util.Map;

public class WeatherAdviceManager {

    private static final Map<String, String> weatherAdviceMap = new HashMap<>();

    static {
        // Initialize the map with weather condition and advice
        weatherAdviceMap.put("rain", "Carry an umbrella and wear waterproof shoes! ");
        weatherAdviceMap.put("drizzle", "Light drizzle. A light raincoat should suffice. ");
        weatherAdviceMap.put("snow", "Snow incoming! Stay warm and wear sturdy boots. ");
        weatherAdviceMap.put("mist", "Misty conditions outside. Use caution while driving and keep your lights on. ");
        weatherAdviceMap.put("smoke", "Smoke in the air. Minimize outdoor activity and wear a mask if necessary. ");
        weatherAdviceMap.put("haze", "Hazy weather. Visibility might be reduced; be cautious while driving. ");
        weatherAdviceMap.put("dust", "Dust in the air. Close windows and wear protective eyewear if outdoors. ");
        weatherAdviceMap.put("fog", "Foggy conditions. Drive slowly and use fog lights to improve visibility. ");
        weatherAdviceMap.put("sand", "Sandy weather. Avoid outdoor activities and protect your belongings. ");
        weatherAdviceMap.put("ash", "Volcanic ash detected. Stay indoors and avoid inhalation. ");
        weatherAdviceMap.put("squall", "Squalls expected. Secure loose items outdoors and stay safe inside. ");
        weatherAdviceMap.put("tornado", "Tornado warning! Seek shelter immediately in a safe location. ");
        weatherAdviceMap.put("clear", "Clear skies ahead! Enjoy the good weather. ");
        weatherAdviceMap.put("clouds", "Cloudy skies. Keep an umbrella handy just in case!");
    }

    public static String getWeatherAdvice(WeatherInfo weatherInfo) {
        if (weatherInfo == null || weatherInfo.getMain() == null) {
            return "";
        }
        String main = weatherInfo.getMain().toLowerCase();
        return weatherAdviceMap.get(main) != null ? weatherAdviceMap.get(main) : "";
    }
}
