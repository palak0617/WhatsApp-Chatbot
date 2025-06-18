package com.ngo.chatbot.whatsappchatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WhatsappChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsappChatbotApplication.class, args);
    }

    // Add this to WhatsappChatbotApplication.java
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}