package com.example.weather.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "agro"
,url = "https://www.ventusky.com/tashkent?ajax=")
public interface AssalomAgroClient {
    @GetMapping(consumes = "tex/html")
    String getData();
}
