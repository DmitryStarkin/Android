package com.hplasplas.task5.Services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat.Builder;

import com.hplasplas.task5.Activitys.R;
import com.hplasplas.task5.Activitys.SettingsActivity;
import com.starsoft.longRuningReceverUtil.Services.LongRunningBroadcastService;
import com.starsoft.longRuningReceverUtil.Util.AlarmManagerUtil;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class NotificationService extends LongRunningBroadcastService {

    final int NOTIFY_ID = 1;
    final int ALARM_PENDING_INTENT_ID = 0;
    SharedPreferences myDefaultPreferences = null;


    public NotificationService() {

        super("com.hplasplas.task5.Services.NotificationService");
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected boolean handleIntent(Intent intent) {

        Intent nextIntent;
        myDefaultPreferences = getDefaultPreferences();
        long lastAlarmSetTime;
        long previousAlarmInterval;
        long notificationInterval;
        int unReadNotificationsCounter;
        String message;

        //TODO check if read one
        if (intent.getBooleanExtra("currentNotificationRead", false)) {
            Editor preferencesEditor = myDefaultPreferences.edit();
            unReadNotificationsCounter = myDefaultPreferences.getInt("unReadNotificationsCounter", 0);
            preferencesEditor.putLong("unReadNotificationsCounter", --unReadNotificationsCounter);
            preferencesEditor.commit();
            if (intent.getBooleanExtra("SettingActivityNeedStart", false)) {
                Intent startActivityIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
                startActivity(startActivityIntent);
            }
            return true;
        }

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            nextIntent = new Intent(this.getApplicationContext(), NotificationService.class);
        } else {
            nextIntent = intent;
        }

        if (!myDefaultPreferences.getBoolean("notifications_enabled", false)) {
            AlarmManagerUtil.cancelServiceAlarm(this.getApplicationContext(), nextIntent, ALARM_PENDING_INTENT_ID);
            Editor preferencesEditor = myDefaultPreferences.edit();
            preferencesEditor.putLong("lastAlarmSetTime", 0);
            preferencesEditor.putLong("previousAlarmInterval", 0);
            preferencesEditor.commit();
            return true;
        }
        message = myDefaultPreferences.getString("notifications_text", getResources().getString(R.string.default_notifications_text));
        notificationInterval = Long.parseLong(myDefaultPreferences.getString("notification_interval", "180000"));

        if (myDefaultPreferences.getBoolean("firstStart", true)) {
            lastAlarmSetTime = 0;
            unReadNotificationsCounter =0;
            previousAlarmInterval = notificationInterval;
            Editor preferencesEditor = myDefaultPreferences.edit();
            preferencesEditor.putLong("lastAlarmSetTime", lastAlarmSetTime);
            preferencesEditor.putLong("previousAlarmInterval", previousAlarmInterval);
            preferencesEditor.putLong("unReadNotificationsCounter", unReadNotificationsCounter);
            preferencesEditor.putBoolean("firstStart", false);
            preferencesEditor.commit();
        } else {
            lastAlarmSetTime = myDefaultPreferences.getLong("lastAlarmSetTime", 0);
            previousAlarmInterval = myDefaultPreferences.getLong("previousAlarmInterval", 0);

            if (previousAlarmInterval == 0) {
                previousAlarmInterval = notificationInterval;
                Editor preferencesEditor = myDefaultPreferences.edit();
                preferencesEditor.putLong("previousAlarmInterval", previousAlarmInterval);
                preferencesEditor.commit();
            }
            unReadNotificationsCounter = myDefaultPreferences.getInt("unReadNotificationsCounter", 0);

        }
        //TODO need logic implement


        showNotifications(message, unReadNotificationsCounter, 1);
        AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), nextIntent,  notificationInterval, ALARM_PENDING_INTENT_ID);
        return true;
    }

    private void showNotifications(String message, int StartNotificationNumber, int notificationCount) {

        String expandedMessage = message + getResources().getString(R.string.notifications_extra_text);

        Intent startServiceIntent = new Intent(this.getApplicationContext(), NotificationService.class);
        Intent startActivityIntent = new Intent(this.getApplicationContext(), NotificationService.class);
        startServiceIntent.putExtra("currentNotificationRead", true);
        startActivityIntent.putExtra("currentNotificationRead", true);
        startActivityIntent.putExtra("SettingActivityNeedStart", true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Builder mNotifyBuilder = (Builder) new Builder(this)
                .setContentTitle(getResources().getString(R.string.notifications_title_text))
                .setSmallIcon(R.mipmap.ic_launcher);


        for (int i = StartNotificationNumber; i < notificationCount; i++) {
            PendingIntent pendingServiceIntent = PendingIntent.getService(this.getApplicationContext(), i, startServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            PendingIntent pendingActivityIntent = PendingIntent.getService(this.getApplicationContext(), i, startActivityIntent, PendingIntent.FLAG_ONE_SHOT);
            if (i>9 & i % 10 == 0) {
                mNotifyBuilder.setContentText(expandedMessage).setNumber(i);
            } else {
                mNotifyBuilder.setContentText(message).setNumber(i);
            }
            mNotifyBuilder
                    .setContentIntent(pendingActivityIntent).setNumber(i)
                    .setDeleteIntent(pendingServiceIntent).setNumber(i)
                    .setAutoCancel(true).setNumber(i);
            mNotificationManager.notify(NOTIFY_ID, mNotifyBuilder.build());
        }
    }


    private SharedPreferences getDefaultPreferences() {

        if (myDefaultPreferences == null) {
            myDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        return myDefaultPreferences;
    }


}
