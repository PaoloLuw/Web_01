package com.luwliette.ztmelody_02.Notification_Show_S;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.luwliette.ztmelody_02.R;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PLAY = "action_play";

    public static Notification notification;

    public static void createNotification(Context context, Track track, int playButton, int pos, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Check if the app has the required notification permission
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // If permission is not granted, request it from the user
                // Note: This should ideally be done in an Activity
                return;
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), track.getImage());

            // Crear la notificación
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_image_default)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getArtist())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true) // mostrar notificación solo una vez
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}
