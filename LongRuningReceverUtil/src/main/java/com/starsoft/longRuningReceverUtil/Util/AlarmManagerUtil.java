package com.starsoft.longRuningReceverUtil.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class AlarmManagerUtil {

    private static void setUpAlarm(AlarmManager alarmManager, PendingIntent pi, long timeInterval) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + timeInterval, pi);
            alarmManager.setAlarmClock(alarmClockInfo, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeInterval, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeInterval, pi);
        }
    }

    public static synchronized boolean isServiceAlarmOn(Context context, Intent intent, int identifier) {

        PendingIntent pi = PendingIntent.getService(context, identifier, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public static synchronized boolean isBroadcastAlarmOn(Context context, Intent intent, int identifier) {

        PendingIntent pi = PendingIntent.getBroadcast(context, identifier, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public static synchronized void setBroadcastAlarm(Context context, Intent intent, long timeInterval, int identifier) {

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getBroadcast(context, identifier, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        setUpAlarm(am, pi, timeInterval);
    }

    public static synchronized void setServiceAlarm(Context context, Intent intent, long timeInterval, int identifier) {

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getService(context, identifier, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        setUpAlarm(am, pi, timeInterval);
    }

    public static synchronized void cancelServiceAlarm(Context context, Intent intent, int identifier) {

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getService(context, identifier, intent, PendingIntent.FLAG_NO_CREATE);
        if(pi!=null){
            am.cancel(pi);
            pi.cancel();
        }

    }
}