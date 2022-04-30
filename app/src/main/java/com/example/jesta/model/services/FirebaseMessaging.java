package com.example.jesta.model.services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("token",token);
        System.out.println("peleg - tocken was printed");

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        System.out.println("peleg - onMessageReceived " + message.getData().toString());
    }


    public void sendToActivity(){
        Intent intent = new Intent();
    }
}
