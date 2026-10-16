package com.marwan.btpweather.controller;

import com.marwan.btpweather.model.WeatherSummary;
import com.marwan.btpweather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicInfo() {
        return ResponseEntity.ok(Map.of(
            "app", "BTP Weather POC",
            "author", "Marwan Soltan",
            "stack", "Spring Boot 3 · SAP BTP Cloud Foundry · OpenWeatherMap",
            "status", "running"
        ));
    }

    @GetMapping
    public ResponseEntity<WeatherSummary> getWeather(
            @RequestParam(defaultValue = "Frankfurt") String city) {
        log.info("GET /api/weather?city={}", city);
        WeatherSummary summary = weatherService.getWeather(city, "api-user");
        return ResponseEntity.ok(summary);
    }
}
