package com.ps.weatherapp.services;

import com.ps.weatherapp.configurations.AppConstants;
import com.ps.weatherapp.interfaces.ICityWeatherService;
import com.ps.weatherapp.models.*;
import com.ps.weatherapp.utilities.ExternalAPIManager;
import com.ps.weatherapp.utilities.FileManager;

import com.ps.weatherapp.utilities.DateTimeManager;
import com.ps.weatherapp.utilities.WeatherAdviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityWeatherService implements ICityWeatherService {

    private final ExternalAPIManager apiManager;
    private final FileManager<CityWeatherDetails> fileManager;
    private final Map<String, CityWeatherDetails> weatherBackUpData;
    private static final Logger logger = LoggerFactory.getLogger(CityWeatherService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.count}")
    private int count;

    @Value("${weather.api.service.type}")
    private String serviceType;

    @Value("${service.online}")
    private boolean isOnline;

    @Value("${service.cacheFileName}")
    private String fileName;

    @Value("${service.update.backup.duration.threshold}")
    private int durationThreshold;

    public CityWeatherService(FileManager<CityWeatherDetails> fileManager, ExternalAPIManager apiManager, Map<String, CityWeatherDetails> weatherBackUpData) {
        this.apiManager = apiManager;
        this.fileManager = fileManager;
        this.weatherBackUpData = weatherBackUpData;
    }

    @Override
    public Mono<CityWeatherAdvice> getWeatherAdvice(String city) {
        if (!isOnline) {
            logger.info(AppConstants.SERVICE_OFFLINE);
            return getWeatherAdviceFromBackUp(city)
                .switchIfEmpty(Mono.just(new CityWeatherAdvice(AppConstants.SERVICE_TEMPORARILY_UNAVAILABLE, new LinkedHashMap<>(), 503)));
        }

        return getCityWeatherDetails(city)
            .flatMap(cityWeatherDetails -> {
                if (cityWeatherDetails.getCod().equals("200")) {
                    return Mono.just(generateWeatherAdvice(cityWeatherDetails));
                } else {
                    return getWeatherAdviceFromBackUp(city)
                    .switchIfEmpty(Mono.just(new CityWeatherAdvice(cityWeatherDetails.getMessage(),
                        new LinkedHashMap<>(),
                        Integer.parseInt(cityWeatherDetails.getCod()))));
                    }
                })
            .onErrorResume(error -> {
                logger.error(error.getMessage());
                return getWeatherAdviceFromBackUp(city)
                    .switchIfEmpty(Mono.just(new CityWeatherAdvice(AppConstants.SERVICE_TEMPORARILY_UNAVAILABLE, new LinkedHashMap<>(), 503)));
            });
    }

    private Mono<CityWeatherDetails> getCityWeatherDetails(String city) {
        Map<String, Object> queryParams = createQueryParams(city);

        return apiManager.fetchData(serviceType, queryParams, CityWeatherDetails.class)
            .doOnNext(cityWeatherDetails -> {
                if ("200".equals(cityWeatherDetails.getCod())) {
                    Mono.fromRunnable(() -> {
                        if (weatherBackUpData.get(city) != null) {
                            setLatestCityWeatherDetailsAndUpdateBackUp(city, cityWeatherDetails);
                        } else {
                            updateCityWeatherBackUp(city, cityWeatherDetails);
                        }
                    }).subscribeOn(Schedulers.boundedElastic()).subscribe();
                }
            })
            .onErrorResume(error -> {
                logger.error(AppConstants.ERROR_MESSAGE_CITY_WEATHER_GET, city, error.getMessage());
                return Mono.just(new CityWeatherDetails(AppConstants.SERVICE_TEMPORARILY_UNAVAILABLE, Collections.emptyList(), "503"));
            });
    }

    private void setLatestCityWeatherDetailsAndUpdateBackUp(String city, CityWeatherDetails cityWeatherDetails) {
        WeatherDetails currentWeatherDetail = cityWeatherDetails.getList().getFirst();
        WeatherDetails existingWeatherDetails = weatherBackUpData.get(city).getList().getFirst();

        LocalDateTime lastUpdatedDatetime = LocalDateTime.parse(weatherBackUpData.get(city).getLastUpdated(), formatter);

        long minutesDifference = ChronoUnit.MINUTES.between(LocalDateTime.now(ZoneOffset.UTC), lastUpdatedDatetime);
        boolean isWeatherSame = haveSameWeatherIds(currentWeatherDetail, existingWeatherDetails);

        //Update back up only when weather changes or last updated duration crosses threshold
        if (!isWeatherSame || Math.abs(minutesDifference) > durationThreshold) {
            updateCityWeatherBackUp(city, cityWeatherDetails);
        }
    }

    private void updateCityWeatherBackUp(String city, CityWeatherDetails cityWeatherDetails) {
        cityWeatherDetails.setLastUpdated(LocalDateTime.now(ZoneOffset.UTC).format(formatter));
        updateWeatherBackUpFile(city, cityWeatherDetails);
    }

    private void updateWeatherBackUpFile(String city, CityWeatherDetails cityWeatherDetails) {
        weatherBackUpData.put(city, cityWeatherDetails);
        fileManager.saveDataToFile(fileName, weatherBackUpData, city);
    }

    private Map<String, Object> createQueryParams(String city) {
        return Map.of(
                AppConstants.REQUEST_PARAM_QUERY, city,
                AppConstants.REQUEST_PARAM_COUNT, count,
                AppConstants.REQUEST_PARAM_APP_ID, apiKey
        );
    }

    //Generate weather advice based on weather conditions
    private CityWeatherAdvice generateWeatherAdvice(CityWeatherDetails cityWeatherDetails) {

        // Map to store weather predictions for each day. The key is the date, and the value is the list of predictions for that day
        Map<String, List<WeatherPrediction>> map = new LinkedHashMap<>();

        // List to store weather predictions for a specific day (in 3-hour intervals)
        List<WeatherPrediction> datedList = new ArrayList<>();

        // Get the city's time zone offset from the city details
        City cityDetails = cityWeatherDetails.getCity();
        int timeZone = cityDetails.getTimezone();

        // Extract the date from the first weather data entry (assuming the first entry represents the start of the forecast)
        String currentDate = DateTimeManager.extractDateFromUnix(cityWeatherDetails.getList().getFirst().getDt(), timeZone);

        // Iterate through each weather detail entry to generate the forecast
        for (WeatherDetails weatherDetails : cityWeatherDetails.getList()) {

            // Instantiate a new WeatherPrediction object for each weather detail
            WeatherPrediction prediction = new WeatherPrediction();

            // Get the date and time for this specific weather entry based on the time zone
            String cityDate = DateTimeManager.extractDateFromUnix(weatherDetails.getDt(), timeZone);
            String cityTime = DateTimeManager.extractTimeFromUnix(weatherDetails.getDt(), timeZone);

            // Set the time of this weather prediction
            prediction.setTime(cityTime);

            // Convert the temperature from Kelvin to Celsius and round to the nearest integer
            double tempCelsius = Double.parseDouble(decimalFormat.format(weatherDetails.getMain().getTemp() - 273.15));
            prediction.setTemperature(tempCelsius);

            // Create a list to store weather status (e.g., description and main weather type)
            List<WeatherStatus> statusList = new ArrayList<>();
            StringBuilder advice = new StringBuilder();

            // Iterate through the weather conditions and build the advice and status list
            weatherDetails.getWeather().forEach(weather -> {
                // Create a WeatherStatus object for each condition and add to the status list
                WeatherStatus weatherStatus = new WeatherStatus();
                weatherStatus.setDescription(weather.getDescription());
                weatherStatus.setStatus(weather.getMain());
                statusList.add(weatherStatus);

                // Append the weather-specific advice to the advice builder
                advice.append(WeatherAdviceManager.getWeatherAdvice(weather));
            });

            // Set the weather status list to the prediction
            prediction.setWeather(statusList);

            // Get the wind details for this weather entry
            Wind wind = weatherDetails.getWind();
            double windSpeed = wind.getSpeed();

            // Advice based on temperature (if above 40°C)
            if (tempCelsius > 40) {
                advice.append("Use sunscreen lotion. ");
            }

            // Advice based on wind speed (if above 10 m/s)
            if (windSpeed > 10) {
                advice.append("It’s too windy, watch out! ");
            }

            // Set the final advice for this prediction (or a default message if no advice)
            prediction.setAdvice(!advice.isEmpty() ? advice.toString() : "No advice as of now!!");

            // Check if the date has changed to store weather predictions for a new day
            if (!currentDate.equals(cityDate)) {
                datedList = new ArrayList<>();  // Clear the dated list for the new day
                currentDate = cityDate; // Update the current date
            }

            // Add the prediction to the list for the current date
            datedList.add(prediction);

            // Store the list of predictions for this date in the map
            map.put(cityDate, datedList);
        }

        // Create and return the generated weather advice for the city
        return new CityWeatherAdvice("success", map, 200);
    }

    private Mono<CityWeatherAdvice> getWeatherAdviceFromBackUp(String city) {
        CityWeatherDetails cityWeatherDetails = weatherBackUpData.get(city);
        if (cityWeatherDetails != null) {
            //filter out stale data and keep present and future data
            CityWeatherDetails weatherDetailsPostFilter = getFutureAndPresentWeatherDetails(city, cityWeatherDetails);
            boolean isDataFiltered = weatherDetailsPostFilter.getCnt() != cityWeatherDetails.getCnt();
            CityWeatherDetails finalCityWeatherDetails = isDataFiltered ? weatherDetailsPostFilter : cityWeatherDetails;
            return Mono.just(generateWeatherAdvice(finalCityWeatherDetails))
                .doOnNext(details -> {
                    if (isDataFiltered) {
                        updateWeatherBackUpFile(city, finalCityWeatherDetails);
                    }
                });
        }
        logger.info(AppConstants.CITY_WEATHER_BACKUP_DATA_UNAVAILABLE, city);
        return Mono.empty();
    }

    private CityWeatherDetails getFutureAndPresentWeatherDetails(String city, CityWeatherDetails cachedWeather) {
        // Get the current UTC time
        LocalDateTime currentUtcTime = LocalDateTime.now(ZoneOffset.UTC);

        // Filter the list to only include records where "dtTxt" is in the future
        // Keep only future records
        List<WeatherDetails> filteredList = cachedWeather.getList().stream()
            .filter(weather -> {
                LocalDateTime dt = LocalDateTime.parse(weather.getDtTxt(), formatter);
                return !dt.isBefore(currentUtcTime); // Keep records that are equal to or after the current time
            }).collect(Collectors.toList());
        cachedWeather.setList(filteredList);
        cachedWeather.setCnt(filteredList.size());
        updateWeatherBackUpFile(city, cachedWeather);
        return cachedWeather;
    }

    private boolean haveSameWeatherIds(WeatherDetails details1, WeatherDetails details2) {
        // Extract weather IDs from the first WeatherDetails object
        Set<Integer> ids1 = details1.getWeather().stream()
                .map(WeatherInfo::getId)
                .collect(Collectors.toSet());

        // Extract weather IDs from the second WeatherDetails object
        Set<Integer> ids2 = details2.getWeather().stream()
                .map(WeatherInfo::getId)
                .collect(Collectors.toSet());

        // Compare the sets
        return ids1.equals(ids2);
    }
}

