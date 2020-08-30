package com.example.vocabase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(context));
        SharedPreferences.Editor sharedPrefEditor = prefs.edit();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, ReviseActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "Daily Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Daily Notification");
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, "default");
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("{Time to revise the vocabulary}")
                .setContentTitle("VocaBase")
                .setContentText("Time to revise the vocabulary")
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        if (nm != null) {
            nm.notify(1, b.build());
            Calendar nextNotifyTime = Calendar.getInstance();
            nextNotifyTime.add(Calendar.DATE, 1);
            sharedPrefEditor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
            sharedPrefEditor.apply();
        }
    }
}