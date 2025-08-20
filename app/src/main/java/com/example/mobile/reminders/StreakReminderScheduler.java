package com.example.mobile.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class StreakReminderScheduler {
    private static PendingIntent buildPI(Context ctx, int reqCode) {
        Intent i = new Intent(ctx, StreakReminderReceiver.class);
        return PendingIntent.getBroadcast(ctx, reqCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static long nextTimeMillis(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        return c.getTimeInMillis();
    }

    public static void scheduleDaily(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        // 20:00
        am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextTimeMillis(20, 0),
                buildPI(ctx, 1200)
        );

        // 22:30
        am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextTimeMillis(22, 30),
                buildPI(ctx, 12230)
        );
    }

    public static void cancelAll(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        am.cancel(buildPI(ctx, 1200));
        am.cancel(buildPI(ctx, 12230));
    }
}
