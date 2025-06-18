package com.ngo.chatbot.whatsappchatbot.service;

import com.ngo.chatbot.whatsappchatbot.model.User;
import com.ngo.chatbot.whatsappchatbot.model.WhatsAppMessage;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final FirebaseService firebaseService;

    @Value("${twilio.whatsapp.number}")
    private String twilioWhatsappNumber;

    public void handleIncomingMessage(String from, String messageBody) {
        String phoneNumber = from.replace("whatsapp:", "");
        String messageText = messageBody.toLowerCase();
        log.info("Received message: {} from: {}", messageText, phoneNumber);

        firebaseService.getUser(phoneNumber)
                .thenAccept(user -> {
                    if (user == null || user.getPhoneNumber() == null) {
                        user = new User();
                        user.setPhoneNumber(phoneNumber);
                        user.setCurrentState("NEW");
                        log.info("New user created: {}", phoneNumber);
                    }
                    processMessage(user, messageText);
                });
    }

    private void processMessage(User user, String messageText) {
        String response;

        switch (messageText) {
            case "help":
                response = "Welcome to Jarurat Care! We provide healthcare consultancy " +
                        "services in Mumbai. You can:\n" +
                        "- Type 'consult' to schedule a consultation\n" +
                        "- Type 'volunteer' to join our team\n" +
                        "- Type 'help' to see this message again";
                break;

            case "consult":
                user.setCurrentState("AWAITING_CONSULTATION_NAME");
                response = "Please provide your name to schedule a consultation.";
                break;

            case "volunteer":
                user.setCurrentState("AWAITING_VOLUNTEER_INFO");
                response = "Thank you for your interest in volunteering! " +
                        "Please share your name and area of expertise.";
                break;

            default:
                processStateBasedMessage(user, messageText);
                return;
        }

        user.setLastMessage(messageText);
        user.setLastInteractionTime(System.currentTimeMillis());
        log.info("Saving user after standard message: {}", user);
        firebaseService.saveUser(user);

        sendWhatsAppMessage(user.getPhoneNumber(), response);
    }

    private void processStateBasedMessage(User user, String messageText) {
        String response;

        switch (user.getCurrentState()) {
            case "AWAITING_CONSULTATION_NAME":
                user.setName(messageText);
                user.setCurrentState("AWAITING_CONSULTATION_ISSUE");
                response = "Hello " + messageText + "! Please describe your health concern briefly.";
                break;

            case "AWAITING_CONSULTATION_ISSUE":
                user.setConsultationIssue(messageText);
                user.setCurrentState("COMPLETED");
                response = "Thank you! We've recorded your consultation request. " +
                        "Our team will contact you within 24 hours.";
                break;

            case "AWAITING_VOLUNTEER_INFO":
                user.setVolunteerInfo(messageText);
                user.setCurrentState("COMPLETED");
                response = "Thank you for providing your information! " +
                        "Our volunteer coordinator will reach out to you soon.";
                break;

            default:
                response = "I'm not sure how to help with that. Type 'help' to see available options.";
        }

        user.setLastMessage(messageText);
        user.setLastInteractionTime(System.currentTimeMillis());
        log.info("Saving user after state message: {}", user);
        firebaseService.saveUser(user);

        sendWhatsAppMessage(user.getPhoneNumber(), response);
    }

    private void sendWhatsAppMessage(String to, String messageText) {
        try {
            Message.creator(
                    new PhoneNumber("whatsapp:" + to),
                    new PhoneNumber("whatsapp:" + twilioWhatsappNumber),
                    messageText
            ).create();

            log.info("Message sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Error sending message to {}: {}", to, e.getMessage());
        }
    }
}
