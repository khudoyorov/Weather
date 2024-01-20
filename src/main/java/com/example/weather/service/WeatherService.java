package com.example.weather.service;

import com.example.weather.client.AssalomAgroClient;
import com.example.weather.client.WeatherClient;
import com.example.weather.dto.AgroDto;
import com.example.weather.dto.InformationDto;
import com.example.weather.dto.WeatherDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherClient weatherClient;
    private final AssalomAgroClient agroClient;

    public ResponseEntity<List<InformationDto>> getWeather(Integer startYear, Integer endYear) {
        Calendar.getInstance().get(Calendar.YEAR);
        if (Calendar.getInstance().get(Calendar.YEAR) < endYear) {
            return ResponseEntity.badRequest().body(null);
        } else if (startYear >= endYear) {
            return ResponseEntity.badRequest().body(null);
        } else if (endYear - startYear > 20) {
            return ResponseEntity.badRequest().body(null);
        }

        List<InformationDto> list = new ArrayList<>();
        for (; startYear <= endYear; startYear++) {
            list.add(getYearInfo(startYear));
        }
        return ResponseEntity.ok(list);
    }


    public ResponseEntity<InformationDto> getByYear(Integer year) {

        Calendar.getInstance().get(Calendar.YEAR);
        if (Calendar.getInstance().get(Calendar.YEAR) < year) {
            return ResponseEntity.badRequest().body(null);
        }


        return ResponseEntity.ok(getYearInfo(year));
    }


    private InformationDto getYearInfo(Integer year) {
        List<WeatherDto> weathersList = weatherClient.getData(year + "-02-01", year + "-05-30");

        // Parsing Fahrenheit to Celsius
        weathersList = weathersList.stream().map(a -> {
            a.setTAVG((a.getTAVG() - 32) * 5 / 9);
            return a;
        }).peek(System.out::println).toList();

        InformationDto information = new InformationDto(String.valueOf(year));


        // Finding the wake-up date
        int wakeUpIndex = 0;
        for (int i = 0; i < weathersList.size(); i++) {
            if (weathersList.get(i).getTAVG() >= 15) {
                wakeUpIndex = i;
                break;
            }
        }
        information.setWakeUpDate(weathersList.get(wakeUpIndex).getDATE());


        // Finding the reproduction date
        int reproducingIndex = 0, counter = 0;
        for (int i = wakeUpIndex; i < weathersList.size(); i++) {
            counter += weathersList.get(i).getTAVG();
            if (counter >= 500) {
                reproducingIndex = i;
                break;
            }
        }
        information.setReproductionDate(weathersList.get(reproducingIndex).getDATE());

        System.out.println(information);
        return information;
    }

    public ResponseEntity<List<AgroDto>> writeData(String fileName) {

        List<AgroDto> list = new ArrayList<>(getTodayWeather());
        File file = new File("D:\\NT\\Weather\\" + fileName + ".xls");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Weather Data");
        if (file.exists()) {
            list.addAll(0, readData(fileName));
        }

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        int rowid = 0;
        HSSFRow row = sheet.createRow(rowid++);
        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("Date and Time");

        cell = row.createCell(1);
        cell.setCellValue("Temperature");

        cell = row.createCell(2);
        cell.setCellValue("Rain");

        cell = row.createCell(3);
        cell.setCellValue("Rain probability");

        cell = row.createCell(4);
        cell.setCellValue("Wind icon");

        cell = row.createCell(5);
        cell.setCellValue("Wind speed");
        for (AgroDto agro : list) {

            row = sheet.createRow(rowid++);

            cell = row.createCell(0);
            cell.setCellValue(agro.getDateTime());

            cell = row.createCell(1);
            cell.setCellValue(agro.getTemp());

            cell = row.createCell(2);
            cell.setCellValue(agro.getRain());

            cell = row.createCell(3);
            cell.setCellValue(agro.getProbLine());

            cell = row.createCell(4);
            cell.setCellValue(agro.getWindIco());

            cell = row.createCell(5);
            cell.setCellValue(agro.getSpeedOfWind());
        }

        try {
            workbook.write(out);
            out.close();
            return ResponseEntity.ok(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private List<AgroDto> readData(String fileName) {

        List<AgroDto> list = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(new File("D:\\NT\\Weather\\" + fileName + ".xls"))) {
            Workbook workbook = new HSSFWorkbook(in);

            Sheet sheet = workbook.getSheetAt(0);
            AgroDto agroDto;
            Iterator<Row> iterator = sheet.iterator();
            iterator.next();
            Row row;
            while (iterator.hasNext()) {
                agroDto = new AgroDto();
                row = iterator.next();
                agroDto.setDateTime(row.getCell(0).getStringCellValue());
                agroDto.setTemp(row.getCell(1).getStringCellValue());
                agroDto.setRain(row.getCell(2).getStringCellValue());
                agroDto.setProbLine(row.getCell(3).getStringCellValue());
                agroDto.setWindIco(row.getCell(4).getStringCellValue());
                agroDto.setSpeedOfWind(row.getCell(5).getStringCellValue());
                list.add(agroDto);
            }
            list.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    List<AgroDto> getTodayWeather() {
        String data = agroClient.getData();
//        System.out.println(data);
        int body = data.indexOf("teplota") + 7;
        data = data.substring(body, data.indexOf("tbody", body));

        List<AgroDto> list = new ArrayList<>(24);
        AgroDto agro;

        LocalDateTime dateTime = LocalDate.now().atStartOfDay();
        dateTime = dateTime.plusHours(1);

        for (int i = 0; i < 24; i++) {
            agro = new AgroDto();
            agro.setDateTime(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            dateTime = dateTime.plusHours(1);

            agro.setTemp(data.substring(
                    data.lastIndexOf(">", data.indexOf("&deg")) + 1
                    , data.indexOf("&deg")
            ) + "C°");

            agro.setRain(data.substring(
                    data.lastIndexOf(">", data.indexOf("mm<")) + 1
                    , data.indexOf("mm<")
            ) + "mm");

            agro.setProbLine(data.substring(
                    data.lastIndexOf(">", data.indexOf("%<")) + 1
                    , data.indexOf("%<")
            ) + "%");

            agro.setWindIco(data.substring(
                    data.indexOf(">", data.indexOf("wind_ico")) + 1
                    , data.indexOf("<", data.indexOf("wind_ico"))
            ));

            agro.setSpeedOfWind(data.substring(
                    data.lastIndexOf(">", data.indexOf("km/h")) + 1
                    , data.indexOf("km/h")
            ) + "km/h");

            list.add(agro);

            body = data.indexOf("teplota") + 7;
            data = data.substring(body);
        }

        return list;

    }
}