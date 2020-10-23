package com.young.ws;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String control = remoteMessage.getData().get("control");
        String data = remoteMessage.getData().get("data");
        Log.d(Contants.LOG_TAG, title+" "+control+" "+data);

        Intent intent = new Intent(Contants.NOTIFICATION);
        intent.putExtra(Contants.NOTIFICATION_TITLE, title);
        intent.putExtra(Contants.NOTIFICATION_BODY, body);
        intent.putExtra(Contants.NOTIFICATION_CONTROL, control);
        intent.putExtra(Contants.NOTIFICATION_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
