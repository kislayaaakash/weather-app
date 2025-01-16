package com.ps.weatherapp.interfaces;

import com.ps.weatherapp.models.CityWeatherAdvice;
import reactor.core.publisher.Mono;

public interface ICityWeatherService {

    Mono<CityWeatherAdvice> getWeatherAdvice(String city);
}
