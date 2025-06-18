// src/main/java/com/ngo/chatbot/whatsappchatbot/controller/WebhookController.java
package com.ngo.chatbot.whatsappchatbot.controller;

import com.ngo.chatbot.whatsappchatbot.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final WhatsAppService whatsAppService;

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> handleWebhook(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {
        
        log.info("Received message from: {}", from);
        whatsAppService.handleIncomingMessage(from, body);
        
        return ResponseEntity.ok().build();
    }
}