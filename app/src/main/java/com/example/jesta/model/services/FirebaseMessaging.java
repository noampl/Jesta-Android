package com.example.jesta.model.services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jesta.model.repositories.GraphqlRepository;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        GraphqlRepository.getInstance().addUserToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        GraphqlRepository.getInstance().getAllFavorTransaction(); // refresh the list
    }


    public void sendToActivity() {
        Intent intent = new Intent();
    }
}
