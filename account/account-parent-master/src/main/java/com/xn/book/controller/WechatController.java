package com.xn.book.controller;

import com.xn.book.response.UserLoginRespVO;
import com.xn.book.service.WxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/wx")
public class WechatController {

    @Autowired
    private WxService wxService;

    /**
     * 根据jscode获取openid
     * @param jsCode
     * @return
     */
    @GetMapping("authCode2Session")
    public UserLoginRespVO auth(String jsCode){
        return wxService.auth(jsCode);
    }

    @GetMapping("bindPhone")
    public UserLoginRespVO bindPhone(String code, HttpServletRequest httpServletRequest){

        String token = httpServletRequest.getHeader("token");

        return wxService.bindPhone(code,token);
    }
}
