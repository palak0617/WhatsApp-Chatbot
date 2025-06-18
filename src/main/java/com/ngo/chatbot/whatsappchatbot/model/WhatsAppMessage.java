package com.ngo.chatbot.whatsappchatbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WhatsAppMessage {
    private String from;
    private String messageId;
    private String text;
    private Long timestamp;
}
