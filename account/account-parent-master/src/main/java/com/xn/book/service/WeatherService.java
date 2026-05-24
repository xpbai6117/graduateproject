package com.xn.book.service;

import com.xn.book.response.WeatherRes;

public interface WeatherService {

    WeatherRes getCurrentWeather(String location);
}
