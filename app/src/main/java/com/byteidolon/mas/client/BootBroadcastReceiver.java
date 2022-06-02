package com.byteidolon.mas.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 启动事件监听
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = String.valueOf(BootBroadcastReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.getAction());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                Intent i = context.getPackageManager()
                        .getLeanbackLaunchIntentForPackage(context.getPackageName())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);

            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
//            startServiceByAlarm(context); // another way
        }
    }

    private void startServiceByAlarm(Context context)
    {
        // Get alarm manager.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = context.getPackageManager()
                .getLeanbackLaunchIntentForPackage(context.getPackageName());
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long startTime = System.currentTimeMillis();
        long intervalTime = 60 * 1000;
        // Create repeat alarm.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, pendingIntent);
    }
}
