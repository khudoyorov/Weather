package com.example.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeatherDto {
    String DATE;
    String station;
    Integer TAVG;

    @Override
    public String toString() {
        return "WeatherDto{" +
                "date='" + DATE + '\''+
                ", station='" + station + '\'' +
                ", tavg=" + TAVG +
                '}';
    }
}
