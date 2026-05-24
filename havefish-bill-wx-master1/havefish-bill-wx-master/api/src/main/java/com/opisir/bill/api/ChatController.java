package com.opisir.bill.api;

import com.opisir.bill.util.BigModelNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {
    @Autowired
    BigModelNew bigModelNew;
    @PostMapping("/api/chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        // 调用BigModelNew中的方法来获取AI回复
        bigModelNew.NewQuestion = userMessage;
        try {
            BigModelNew.main(null); // 这里只是为了演示，实际应用中应该重构避免直接调用main方法
        } catch (Exception e) {
            return new ResponseEntity<>("Error communicating with AI service.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 返回AI的回复给小程序
        return new ResponseEntity<>(bigModelNew.totalAnswer, HttpStatus.OK);
    }
}