package com.xn.book.controller;

import com.xn.book.response.WeatherRes;
import com.xn.book.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{location}")
    public WeatherRes getCurrentWeather(@PathVariable("location") String location){
        return weatherService.getCurrentWeather(location);
    }
}
