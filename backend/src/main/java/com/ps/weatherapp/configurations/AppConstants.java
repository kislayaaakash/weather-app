package com.ps.weatherapp.configurations;

public class AppConstants {

    public static final String WEATHER_API_BASE_URL = "https://api.openweathermap.org/data/2.5";

    public static final String SERVICE_TEMPORARILY_UNAVAILABLE = "Service temporarily unavailable.";
    public static final String SERVICE_OFFLINE = "Service running in offline mode.";

    public static final String REQUEST_PARAM_QUERY = "q";
    public static final String REQUEST_PARAM_APP_ID = "appid";
    public static final String REQUEST_PARAM_COUNT = "cnt";

    public static final String ERROR_MESSAGE_CITY_WEATHER_GET = "Error getting weather data for {}: {}";
    public static final String CITY_WEATHER_BACKUP_DATA_UNAVAILABLE = "No data available for {} in back up.";
    public static final String CITY_WEATHER_BACKUP_DATA_FILE_UPDATED = "Back up data file updated";
}
