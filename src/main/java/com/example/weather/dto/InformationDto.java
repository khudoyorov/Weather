package com.example.weather.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InformationDto {
    String year;
    String wakeUpDate;
    String  reproductionDate;


    public InformationDto(String year) {
        this.year = year;
    }
}
