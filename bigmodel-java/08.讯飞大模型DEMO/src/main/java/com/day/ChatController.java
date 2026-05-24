//package com.day;
//
//@RestController
//public class ChatController {
//
//    @PostMapping("/api/chat")
//    public ResponseEntity<String> chat(@RequestBody Map<String, String> request) {
//        String userMessage = request.get("message");
//
//        // 调用BigModelNew中的方法来获取AI回复
//        BigModelNew.NewQuestion = userMessage;
//        try {
//            BigModelNew.main(null); // 这里只是为了演示，实际应用中应该重构避免直接调用main方法
//        } catch (Exception e) {
//            return new ResponseEntity<>("Error communicating with AI service.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        // 返回AI的回复给小程序
//        return new ResponseEntity<>(BigModelNew.totalAnswer, HttpStatus.OK);
//    }
//}