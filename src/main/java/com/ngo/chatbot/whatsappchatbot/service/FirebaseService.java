package com.ngo.chatbot.whatsappchatbot.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngo.chatbot.whatsappchatbot.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final FirebaseDatabase firebaseDatabase;
    private static final String USERS_REF = "users";

    public void saveUser(User user) {
        log.info("Trying to save user to Firebase: {}", user);
        DatabaseReference ref = firebaseDatabase.getReference(USERS_REF)
                .child(user.getPhoneNumber());

        ref.setValue(user, (error, ref1) -> {
            if (error != null) {
                log.error("Error saving user: {}", error.getMessage());
            } else {
                log.info("User saved: {}", user.getPhoneNumber());
            }
        });
    }

    public CompletableFuture<User> getUser(String phoneNumber) {
        CompletableFuture<User> future = new CompletableFuture<>();

        firebaseDatabase.getReference(USERS_REF)
                .child(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        future.complete(user != null ? user : new User());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        log.error("Error fetching user: {}", error.getMessage());
                        future.completeExceptionally(error.toException());
                    }
                });

        return future;
    }
}
