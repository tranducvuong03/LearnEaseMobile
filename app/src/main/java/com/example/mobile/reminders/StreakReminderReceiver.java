package com.example.mobile.reminders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.mobile.R;

public class StreakReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Android 13+: nếu chưa cấp quyền, bỏ qua để tránh crash/không hiện
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // Nếu đã credit streak hôm nay thì không nhắc nữa
        if (com.example.mobile.utils.StreakLocal.hasCreditedToday(context)) return;

        com.example.mobile.utils.NotificationUtils.ensureChannel(context);

        Intent open = new Intent(context, com.example.mobile.SoloSkillActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                context, 1001, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = com.example.mobile.reminders.ReminderMessages.getTitle();
        String body  = com.example.mobile.reminders.ReminderMessages.getBody();

        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, com.example.mobile.utils.NotificationUtils.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fire)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(2001, nb.build());
    }
}
