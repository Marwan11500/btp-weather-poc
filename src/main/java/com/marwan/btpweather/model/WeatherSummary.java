package com.marwan.btpweather.model;

import lombok.Builder;
import lombok.Data;

/**
 * Clean DTO returned by our API to clients.
 * Hides the raw OpenWeatherMap structure and exposes only what we need.
 */
@Data
@Builder
public class WeatherSummary {

    private String city;
    private String country;
    private String condition;       // e.g. "Clear", "Clouds"
    private String description;     // e.g. "clear sky"
    private double temperatureCelsius;
    private double feelsLikeCelsius;
    private int humidityPercent;
    private double windSpeedMs;
    private String source;          // "BTP Destination Service → OpenWeatherMap"
    private String fetchedBy;       // JWT subject (username) from XSUAA token
}
