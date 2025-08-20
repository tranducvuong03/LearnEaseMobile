package com.example.mobile.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            android.app.AlarmManager am = context.getSystemService(android.app.AlarmManager.class);
            if (am != null && !am.canScheduleExactAlarms()) {
                // Chưa có quyền -> KHÔNG schedule exact alarm ở đây để tránh crash
                return;
            }
        }
        StreakReminderScheduler.scheduleDaily(context);
    }
}
