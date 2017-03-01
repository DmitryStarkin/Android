package com.hplasplas.task5.Services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.hplasplas.task5.Activitys.SettingsActivity;
import com.hplasplas.task5.R;
import com.starsoft.longRuningReceverUtil.Services.LongRunningBroadcastService;
import com.starsoft.longRuningReceverUtil.Util.AlarmManagerUtil;

import static com.hplasplas.task5.Setting.Constants.ACTIVITY_PENDING_INTENT_ID;
import static com.hplasplas.task5.Setting.Constants.ALARM_PENDING_INTENT_ID;
import static com.hplasplas.task5.Setting.Constants.DEBUG;
import static com.hplasplas.task5.Setting.Constants.INTERVAL_ACCURACY;
import static com.hplasplas.task5.Setting.Constants.MLL_PER_MIN;
import static com.hplasplas.task5.Setting.Constants.NOTIFICATIONS_PREFERENCES_FILE;
import static com.hplasplas.task5.Setting.Constants.NOTIFY_ID;
import static com.hplasplas.task5.Setting.Constants.SERVICE_PENDING_INTENT_ID;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class NotificationService extends LongRunningBroadcastService {


    private final String TAG = getClass().getSimpleName();


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
        SharedPreferences myDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences notificationPreferences = this.getSharedPreferences(NOTIFICATIONS_PREFERENCES_FILE, MODE_PRIVATE);
        long timeCorrection;
        long sleepTimeInterval;
        long alarmIntervalToSet;
        long startPointTime;
        long previousAlarmInterval;
        long notificationInterval;
        int unReadNotificationsCounter;
        int countMessagesToShow;
        String message;


        if (intent.getBooleanExtra("currentNotificationRead", false)) {
            Editor preferencesEditor = notificationPreferences.edit();
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
            Editor preferencesEditor = notificationPreferences.edit();
            preferencesEditor.putLong("startPointTime", 0);
            preferencesEditor.putLong("previousAlarmInterval", 0);
            preferencesEditor.commit();
            return true;
        }

        message = myDefaultPreferences.getString("notifications_text", getResources().getString(R.string.default_notifications_text));
        notificationInterval = Long.parseLong(myDefaultPreferences.getString("notification_interval", "30")) * MLL_PER_MIN;

        if (notificationPreferences.getBoolean("firstStart", true)) {
            startPointTime = System.currentTimeMillis();
            unReadNotificationsCounter = 0;
            previousAlarmInterval = notificationInterval;
            Editor preferencesEditor = notificationPreferences.edit();
            preferencesEditor.putBoolean("firstStart", false);
            preferencesEditor.commit();
        } else {
            startPointTime = notificationPreferences.getLong("startPointTime", System.currentTimeMillis());
            if (startPointTime == 0) {
                startPointTime = System.currentTimeMillis();
            }
            unReadNotificationsCounter = notificationPreferences.getInt("unReadNotificationsCounter", 0);
            previousAlarmInterval = notificationPreferences.getLong("previousAlarmInterval", 0);
            if (previousAlarmInterval == 0) {
                previousAlarmInterval = notificationInterval;
            }
        }
        sleepTimeInterval = System.currentTimeMillis() - startPointTime;
        timeCorrection = sleepTimeInterval % previousAlarmInterval;
        countMessagesToShow = (int) (sleepTimeInterval / previousAlarmInterval);
        if (timeCorrection > (previousAlarmInterval - INTERVAL_ACCURACY)) {
            countMessagesToShow++;
        }

        startPointTime = startPointTime + countMessagesToShow * previousAlarmInterval;

        alarmIntervalToSet = notificationInterval - timeCorrection;

        if ((alarmIntervalToSet - INTERVAL_ACCURACY) > 0) {
            AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), nextIntent, alarmIntervalToSet, ALARM_PENDING_INTENT_ID);
        } else {
            startPointTime = AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), nextIntent, notificationInterval, ALARM_PENDING_INTENT_ID);
            if (notificationInterval < previousAlarmInterval) {
                countMessagesToShow++;
            }
        }
        previousAlarmInterval = notificationInterval;

        showNotifications(message, unReadNotificationsCounter, countMessagesToShow);

        Editor preferencesEditor = notificationPreferences.edit();
        preferencesEditor.putLong("startPointTime", startPointTime);
        preferencesEditor.putLong("previousAlarmInterval", previousAlarmInterval);
        preferencesEditor.putInt("unReadNotificationsCounter", unReadNotificationsCounter + countMessagesToShow);
        preferencesEditor.commit();

        return true;
    }

    private void showNotifications(String message, int StartNotificationNumber, int notificationToShow) {

        if (notificationToShow != 0) {
            int notificationCounter = StartNotificationNumber + notificationToShow;
            String expandedMessage = message + " " + getResources().getString(R.string.notifications_extra_text) + " " + getResources().getString(R.string.unread_messages);
            String defaultMessage = message + " " + getResources().getString(R.string.unread_messages);

            Intent startServiceIntent = new Intent(this.getApplicationContext(), NotificationService.class);
            startServiceIntent.putExtra("currentNotificationRead", true);
            Intent startActivityIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startActivityIntent.putExtra("currentNotificationRead", true);
            PendingIntent pendingServiceIntent = PendingIntent.getService(this.getApplicationContext(), SERVICE_PENDING_INTENT_ID, startServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent pendingActivityIntent = PendingIntent.getActivity(this.getApplicationContext(), ACTIVITY_PENDING_INTENT_ID, startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Builder mNotifyBuilder = new Builder(this)
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
        }
    }
}



