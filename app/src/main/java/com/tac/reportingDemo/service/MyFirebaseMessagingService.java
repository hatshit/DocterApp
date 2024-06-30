package com.tac.reportingDemo.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.HomeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

//        Map<String, String> extraData = remoteMessage.getData();
//
//        String brandId = extraData.get("brandId");
//        String category = extraData.get("category");

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_noti);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "JAINS_BASKET")
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_noti)
                        .setLargeIcon(icon);

        Intent intent = new Intent(this, HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        int id = (int) System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("JAINSBASKET", "JAINS BASKET", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(id, notificationBuilder.build());

    }

}
