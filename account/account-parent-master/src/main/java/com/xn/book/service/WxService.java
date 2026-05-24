package com.xn.book.service;

import com.xn.book.dto.WxMessageSendDTO;
import com.xn.book.response.UserLoginRespVO;

public interface WxService {

    UserLoginRespVO auth(String jsCode);

    UserLoginRespVO bindPhone(String code,String token);

    Boolean sendMessage(WxMessageSendDTO wxMessageSendDTO);
}
