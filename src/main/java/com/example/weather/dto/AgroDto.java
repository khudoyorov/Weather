package com.example.weather.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class AgroDto {

    String dateTime;
    String temp;
    String rain;
    String probLine;
    String windIco;
    String speedOfWind;


    @Override
    public String toString() {
        return "AgroDto{" +
                "temp='" + temp + '\'' +
                ", rain='" + rain + '\'' +
                ", probLine='" + probLine + '\'' +
                ", windIco='" + windIco + '\'' +
                ", speedOfWind='" + speedOfWind + '\'' +
                '}';
    }
}
