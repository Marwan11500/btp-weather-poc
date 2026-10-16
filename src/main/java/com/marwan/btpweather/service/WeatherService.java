package com.marwan.btpweather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marwan.btpweather.model.WeatherSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Fetches weather data from OpenWeatherMap API.
 *
 * On BTP Cloud Foundry, the API key is injected via environment variable
 * (set in the BTP cockpit or manifest.yml env section) — never hardcoded.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    private static final String OWM_URL =
        "https://api.openweathermap.org/data/2.5/weather?q={city}&units=metric&appid={apiKey}";

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherSummary getWeather(String city, String fetchedBy) {
        log.info("Fetching weather for city='{}' requested by user='{}'", city, fetchedBy);

        try {
            String response = restTemplate.getForObject(OWM_URL, String.class, city, apiKey);
            JsonNode root = objectMapper.readTree(response);

            String condition = root.path("weather").path(0).path("main").asText("Unknown");
            String description = root.path("weather").path(0).path("description").asText("Unknown");
            JsonNode main = root.path("main");

            return WeatherSummary.builder()
                    .city(root.path("name").asText())
                    .country(root.path("sys").path("country").asText())
                    .condition(condition)
                    .description(description)
                    .temperatureCelsius(main.path("temp").asDouble())
                    .feelsLikeCelsius(main.path("feels_like").asDouble())
                    .humidityPercent(main.path("humidity").asInt())
                    .windSpeedMs(root.path("wind").path("speed").asDouble())
                    .source("OpenWeatherMap API via SAP BTP Cloud Foundry")
                    .fetchedBy(fetchedBy)
                    .build();

        } catch (Exception e) {
            log.error("Failed to fetch weather for city='{}': {}", city, e.getMessage(), e);
            throw new RuntimeException("Could not fetch weather data: " + e.getMessage(), e);
        }
    }
}
