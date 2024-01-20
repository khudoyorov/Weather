package com.example.weather.config;

import com.example.weather.client.WeatherClient;
import com.example.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class ScheduleJob {
    private final WeatherService weatherService;
    @Scheduled(cron = "00 00 * * *")
    private void writeData(){
        weatherService.writeData("WeatherFromAssalomAgro");
    }

}
