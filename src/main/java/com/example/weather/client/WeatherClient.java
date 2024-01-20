package com.example.weather.client;

import com.example.weather.dto.WeatherDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "weather-client",
        url = "https://www.ncei.noaa.gov/access/services/data/v1?dataset=daily-summaries&stations=UZM00038457&dataTypes=TAVG&units=standard&format=json")
public interface WeatherClient {
    @GetMapping(consumes = "application/json")
    List<WeatherDto> getData(@RequestParam String startDate, @RequestParam String endDate);
}
