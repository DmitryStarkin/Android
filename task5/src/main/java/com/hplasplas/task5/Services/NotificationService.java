package com.hplasplas.task5.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.hplasplas.task5.Activitys.SettingsActivity;
import com.starsoft.longRuningReceverUtil.Services.LongRunningBroadcastService;
import com.starsoft.longRuningReceverUtil.Util.AlarmManagerUtil;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class NotificationService extends LongRunningBroadcastService {

    SharedPreferences myPreferences = null;


    public NotificationService(String name) {

        super(name);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected boolean handleIntent(Intent intent) {

        Intent nextIntent;
        myPreferences = getPreferences();

        //TODO check if read one
        if (intent.getBooleanExtra("currentNotificationRead", false)) {
            Editor preferencesEditor = myPreferences.edit();
            long unReadNotificationsCounter = myPreferences.getInt("unReadNotificationsCounter", 0);
            preferencesEditor.putLong("unReadNotificationsCounter", --unReadNotificationsCounter);
            preferencesEditor.commit();
            return true;
        }

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            nextIntent = new Intent(this.getApplicationContext(), NotificationService.class);
        } else {
            nextIntent = intent;
        }

        if (!myPreferences.getBoolean("notifications_enabled", false)) {
            AlarmManagerUtil.cancelServiceAlarm(this, nextIntent, 0);
            Editor preferencesEditor = myPreferences.edit();
            preferencesEditor.putLong("lastAlarmSetTime", 0);
            preferencesEditor.commit();
            return true;
        }

        if (myPreferences.getBoolean("firstStart", true)) {
            Editor preferencesEditor = myPreferences.edit();
            preferencesEditor.putLong("lastAlarmSetTime", 0);
            preferencesEditor.putLong("previousAlarmInterval", 0);
            preferencesEditor.putLong("unReadNotificationsCounter", 0);
            preferencesEditor.commit();
        } else {
            long lastAlarmSetTime = myPreferences.getLong("lastAlarmSetTime", 0);
            long previousAlarmInterval = myPreferences.getLong("previousAlarmInterval", 0);
            long notificationInterval = Long.parseLong(myPreferences.getString("notification_interval", "180000"));
            int unReadNotificationsCounter = myPreferences.getInt("unReadNotificationsCounter", 0);
        }






        return true;
    }

    private int showNotifications(int StartNotificationNumber, int endNotificationNumber) {

        //TODO
        return StartNotificationNumber;
    }

    private Notification createNotifications(String message, int number) {
        //TODO

        Intent startServiceIntent = new Intent(this.getApplicationContext(), NotificationService.class);
        Intent startActivityIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
        startServiceIntent.putExtra("currentNotificationRead", true);
        PendingIntent pendingServiceIntent = PendingIntent.getService(this.getApplicationContext(), number, startServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingActivityIntent = PendingIntent.getActivity(this.getApplicationContext(), number, startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        return null;
    }

    private SharedPreferences getPreferences() {

        if (myPreferences == null) {
            myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        return myPreferences;
    }
}
