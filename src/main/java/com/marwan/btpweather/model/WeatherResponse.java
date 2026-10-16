package com.marwan.btpweather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Maps the OpenWeatherMap API JSON response.
 * Using @JsonIgnoreProperties to be resilient to extra fields from the API.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    private String name;        // City name

    @JsonProperty("main")
    private MainWeather main;

    @JsonProperty("weather")
    private List<WeatherDescription> weather;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("sys")
    private Sys sys;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainWeather {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        private int humidity;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherDescription {
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private double speed;
        private int deg;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private String country;
    }
}
