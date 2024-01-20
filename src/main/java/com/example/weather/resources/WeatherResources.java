package com.example.weather.resources;

import com.example.weather.dto.AgroDto;
import com.example.weather.dto.InformationDto;
import com.example.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("weather")
public class WeatherResources {
    private final WeatherService weatherService;

    @PostMapping
    public ResponseEntity<List<AgroDto>> writeData(@RequestParam String fileName){
        return weatherService.writeData(fileName);
    }

    @GetMapping("/{startYear}/{endYear}")
    public ResponseEntity<List<InformationDto>> getWeather(@PathVariable Integer startYear, @PathVariable Integer endYear) {
        return weatherService.getWeather(startYear, endYear);
    }

    @GetMapping("/{year}")
    public ResponseEntity<InformationDto> getByYear(@PathVariable Integer year) {
        return weatherService.getByYear(year);
    }

}
