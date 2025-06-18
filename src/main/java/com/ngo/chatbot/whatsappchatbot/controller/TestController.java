// src/main/java/com/ngo/chatbot/whatsappchatbot/controller/TestController.java
package com.ngo.chatbot.whatsappchatbot.controller;

import com.ngo.chatbot.whatsappchatbot.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final WhatsAppService whatsAppService;

    @PostMapping("/simulate-message")
    public ResponseEntity<String> simulateMessage(
            @RequestParam String phoneNumber,
            @RequestParam String message) {
        try {
            whatsAppService.handleIncomingMessage("whatsapp:" + phoneNumber, message);
            return ResponseEntity.ok("Message processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running");
    }
}