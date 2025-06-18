package com.ngo.chatbot.whatsappchatbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private String phoneNumber;
    private String name;
    private String lastMessage;
    private String currentState;
    private String volunteerInfo;
    private String consultationIssue;
    private Long lastInteractionTime;
}
