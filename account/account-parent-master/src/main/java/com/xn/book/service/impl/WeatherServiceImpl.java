package com.xn.book.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xn.book.response.WeatherRes;
import com.xn.book.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WeatherServiceImpl  implements WeatherService {

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${weather.url}")
    private String url;

    @Value("${weather.key}")
    private String key;

    @Autowired
    private RestTemplate restTemplate;

    private final static String WEATHER = "weather:";


    public static void main(String[] args) {
        System.out.println(String.format("aaa %s bbb %s","1","2"));
    }

    @Override
    public WeatherRes getCurrentWeather(String location) {
        WeatherRes weatherRes = null;
        String weatherUrl = String.format(url, key, location);
        log.info("weatherUrl:" + weatherUrl);

        Object o = redisTemplate.opsForValue().get(WEATHER + location);
        if (StringUtils.isEmpty(o)){
            try {
                ResponseEntity<JSONObject> forEntity = restTemplate.getForEntity(weatherUrl, JSONObject.class);
                if (forEntity.getStatusCode().value() == (HttpStatus.OK.value())){
                    JSONObject body = forEntity.getBody();
                    log.info("weather body : " + body.toJSONString());
                    String status = body.getString("status");
                    if (StringUtils.isEmpty(status)){
                        JSONArray results = body.getJSONArray("results");
                        JSONObject object = results.getJSONObject(0);
                        JSONObject now = object.getJSONObject("now");
                        weatherRes = new WeatherRes();
                        weatherRes.setText(now.getString("text"));
                        weatherRes.setCode(now.getString("code"));
                        weatherRes.setTemperature(now.getString("temperature"));
                        log.info("weatherRes:" + weatherRes.toString());
                    }
                }else {
                    log.error("weather error :" + forEntity.getBody());
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }else {
            JSONObject jsonObject = JSON.parseObject(o.toString());
            weatherRes = new WeatherRes();
            weatherRes.setCode(jsonObject.getString("code"));
            weatherRes.setText(jsonObject.getString("text"));
            weatherRes.setTemperature(jsonObject.getString("temperature"));
        }
        return weatherRes;
    }
}
