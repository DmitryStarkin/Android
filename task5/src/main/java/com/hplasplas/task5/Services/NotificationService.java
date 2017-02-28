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
import android.util.Log;

import com.hplasplas.task5.Activitys.SettingsActivity;
import com.hplasplas.task5.R;
import com.starsoft.longRuningReceverUtil.Services.LongRunningBroadcastService;
import com.starsoft.longRuningReceverUtil.Util.AlarmManagerUtil;

import static com.hplasplas.task5.Setting.Constants.DEBUG;
import static com.hplasplas.task5.Setting.Constants.INTERVAL_ACCURACY;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class NotificationService extends LongRunningBroadcastService {

    final int NOTIFY_ID = 1;
    final int ALARM_PENDING_INTENT_ID = 0;
    final int MLL_PER_MIN = 60000;
    private final String TAG = getClass().getSimpleName();
    SharedPreferences myDefaultPreferences = null;


    public NotificationService() {

        super("com.hplasplas.task5.Services.NotificationService");
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected boolean handleIntent(Intent intent) {

        if (DEBUG) {
            Log.d(TAG, "handleIntent: ");
        }

        Intent nextIntent;
        myDefaultPreferences = getDefaultPreferences();
        long timeCorrection;
        long SleepTimeInterval;
        long alarmIntervalToSet;
        long lastAlarmSetTime;
        long previousAlarmInterval;
        long notificationInterval;
        int unReadNotificationsCounter;
        int messagesToShow;
        String message;


        if (intent.getBooleanExtra("currentNotificationRead", false)) {
            Editor preferencesEditor = myDefaultPreferences.edit();
            preferencesEditor.putInt("unReadNotificationsCounter", 0);
            preferencesEditor.commit();
            return true;
        }

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
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
        notificationInterval = Long.parseLong(myDefaultPreferences.getString("notification_interval", "30")) * MLL_PER_MIN;

        if (myDefaultPreferences.getBoolean("firstStart", true)) {
            lastAlarmSetTime = System.currentTimeMillis();
            unReadNotificationsCounter = 0;
            previousAlarmInterval = notificationInterval;
            Editor preferencesEditor = myDefaultPreferences.edit();
            preferencesEditor.putLong("lastAlarmSetTime", lastAlarmSetTime);
            preferencesEditor.putLong("previousAlarmInterval", previousAlarmInterval);
            preferencesEditor.putInt("unReadNotificationsCounter", unReadNotificationsCounter);
            preferencesEditor.putBoolean("firstStart", false);
            preferencesEditor.commit();
        } else {
            lastAlarmSetTime = myDefaultPreferences.getLong("lastAlarmSetTime", System.currentTimeMillis());
            previousAlarmInterval = myDefaultPreferences.getLong("previousAlarmInterval", 0);

            if (previousAlarmInterval == 0) {
                previousAlarmInterval = notificationInterval;
                Editor preferencesEditor = myDefaultPreferences.edit();
                preferencesEditor.putLong("previousAlarmInterval", previousAlarmInterval);
                preferencesEditor.commit();
            }
            unReadNotificationsCounter = myDefaultPreferences.getInt("unReadNotificationsCounter", 0);
        }
        SleepTimeInterval = System.currentTimeMillis() - lastAlarmSetTime;
        timeCorrection = SleepTimeInterval % previousAlarmInterval;
        messagesToShow = (int) (SleepTimeInterval / previousAlarmInterval);
        if (timeCorrection > (previousAlarmInterval - INTERVAL_ACCURACY)) {
            messagesToShow++;
        }

        alarmIntervalToSet = notificationInterval - timeCorrection;

        if ((alarmIntervalToSet - INTERVAL_ACCURACY) > 0) {
            lastAlarmSetTime = AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), nextIntent, alarmIntervalToSet, ALARM_PENDING_INTENT_ID);
        } else {
            lastAlarmSetTime = AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), nextIntent, notificationInterval, ALARM_PENDING_INTENT_ID);
            if (notificationInterval < previousAlarmInterval) {
                messagesToShow++;
            }
        }
        writeTimes(lastAlarmSetTime, notificationInterval);

        showNotifications(message, unReadNotificationsCounter, messagesToShow);
        return true;
    }

    private void showNotifications(String message, int StartNotificationNumber, int notificationToShow) {

        if (notificationToShow != 0) {
            int notificationCounter = StartNotificationNumber + notificationToShow;
            String expandedMessage = message + " " + getResources().getString(R.string.notifications_extra_text) + " " + getResources().getString(R.string.unread_messages);
            String defaultMessage = message + " " + getResources().getString(R.string.unread_messages);

            Intent startServiceIntent = new Intent(this.getApplicationContext(), NotificationService.class);
            Intent startActivityIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startServiceIntent.putExtra("currentNotificationRead", true);

            PendingIntent pendingServiceIntent = PendingIntent.getService(this.getApplicationContext(), NOTIFY_ID, startServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent pendingActivityIntent = PendingIntent.getActivity(this.getApplicationContext(), NOTIFY_ID, startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            Builder mNotifyBuilder = (Builder) new Builder(this)
                    .setContentTitle(getResources().getString(R.string.notifications_title_text))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingActivityIntent)
                    .setDeleteIntent(pendingServiceIntent)
                    .setAutoCancel(true);

            for (int i = StartNotificationNumber; i < notificationCounter; i++) {
                if (i > 10) {
                    mNotifyBuilder.setContentText(expandedMessage).setNumber(i + 1);
                } else {
                    mNotifyBuilder.setContentText(defaultMessage).setNumber(i + 1);
                }

                mNotificationManager.notify(NOTIFY_ID, mNotifyBuilder.build());
            }
            updateNotificationsCounter(notificationToShow);
        }
    }


    private SharedPreferences getDefaultPreferences() {

        if (myDefaultPreferences == null) {
            myDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        return myDefaultPreferences;
    }

    @SuppressLint("CommitPrefEdits")
    private void writeTimes(long lastAlarmSetTime, long previousAlarmInterval) {

        Editor preferencesEditor = myDefaultPreferences.edit();
        preferencesEditor.putLong("lastAlarmSetTime", lastAlarmSetTime);
        preferencesEditor.putLong("previousAlarmInterval", previousAlarmInterval);
        preferencesEditor.commit();
    }

    @SuppressLint("CommitPrefEdits")
    private void updateNotificationsCounter(int setNotifications) {

        int currentNotificationsCounter = myDefaultPreferences.getInt("unReadNotificationsCounter", 0);
        Editor preferencesEditor = myDefaultPreferences.edit();
        preferencesEditor.putInt("unReadNotificationsCounter", currentNotificationsCounter + setNotifications);
        preferencesEditor.commit();
    }


}



