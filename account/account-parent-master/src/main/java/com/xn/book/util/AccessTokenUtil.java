package com.xn.book.util;

import com.alibaba.fastjson.JSONObject;
import com.xn.book.common.exception.BasicInfoException;
import com.xn.book.common.exception.BasicInfoStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AccessTokenUtil {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private RestTemplate restTemplate;


private final static String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&";
    public String getAccessToken(String appid,String secret){
        try {
            Object o = redisTemplate.opsForValue().get(appid);
            String accessToken = null;
            if (StringUtils.isEmpty(o)) {

                String url = accessTokenUrl + "appid="+appid+"&secret=" + secret;
                ResponseEntity<JSONObject> out = restTemplate.getForEntity(url, JSONObject.class);
                HttpStatus httpStatus = out.getStatusCode();
                String token = "";
                JSONObject body = out.getBody();
                Integer errcode = body.getInteger("errcode");
                if (errcode != null) {
                    if (errcode != 0) throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),body.getString("errmsg"));
                }
                accessToken = body.get("access_token").toString();
                redisTemplate.opsForValue().set("access_token",accessToken,7000, TimeUnit.SECONDS);
            }else {
                accessToken = o.toString();
            }

            return accessToken;
        }catch (Exception e){
            //e.printStackTrace();
            log.info("获取access_token失败："+e.getMessage());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),e.getMessage());
        }
    }
}
