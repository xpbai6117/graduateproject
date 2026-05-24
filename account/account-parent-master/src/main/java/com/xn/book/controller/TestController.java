package com.xn.book.controller;

import com.xn.book.dto.WxMessageSendDTO;
import com.xn.book.service.WxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    WxService wxService;
    @RequestMapping("1")
    public void test(){
        WxMessageSendDTO wxMessageSendDTO = new WxMessageSendDTO();
        wxMessageSendDTO.setOpenid("o6emw5VZWwN-FzF3iMj7EAF_MIho");
        wxMessageSendDTO.setAmount(new BigDecimal("299.01"));
        wxMessageSendDTO.setRemark("您的本月预算即将耗尽，请注意合理消费。");
        wxService.sendMessage(wxMessageSendDTO);
    }
}
