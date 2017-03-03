package com.hplasplas.task5.Services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.hplasplas.task5.Activitys.SettingsActivity;
import com.hplasplas.task5.R;
import com.starsoft.intentServiceUtil.Services.LongRunningBroadcastService;
import com.starsoft.intentServiceUtil.alarmUtil.AlarmManagerUtil;

import static com.hplasplas.task5.Setting.Constants.ACTIVITY_PENDING_INTENT_ID;
import static com.hplasplas.task5.Setting.Constants.ALARM_PENDING_INTENT_ID;
import static com.hplasplas.task5.Setting.Constants.CURRENT_NOTIFICATION_READ;
import static com.hplasplas.task5.Setting.Constants.DEBUG;
import static com.hplasplas.task5.Setting.Constants.DEFAULT_NOTIFICATIONS_INTERVAL;
import static com.hplasplas.task5.Setting.Constants.INTERVAL_ACCURACY;
import static com.hplasplas.task5.Setting.Constants.MLL_PER_MIN;
import static com.hplasplas.task5.Setting.Constants.NEED_ACTIVITY_START;
import static com.hplasplas.task5.Setting.Constants.NOTIFICATIONS_ENABLED;
import static com.hplasplas.task5.Setting.Constants.NOTIFICATIONS_INTERVAL;
import static com.hplasplas.task5.Setting.Constants.NOTIFICATIONS_PREFERENCES_FILE;
import static com.hplasplas.task5.Setting.Constants.NOTIFICATIONS_TEXT;
import static com.hplasplas.task5.Setting.Constants.NOTIFY_ID;
import static com.hplasplas.task5.Setting.Constants.NOTIFY_TAG;
import static com.hplasplas.task5.Setting.Constants.NOT_INITIALIZED;
import static com.hplasplas.task5.Setting.Constants.PREVIOUS_ALARM_INTERVAL;
import static com.hplasplas.task5.Setting.Constants.SERVICE_PENDING_INTENT_ID;
import static com.hplasplas.task5.Setting.Constants.START_POINT_TIME;
import static com.hplasplas.task5.Setting.Constants.UNREAD_NOTIFICATIONS_COUNTER;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class NotificationService extends LongRunningBroadcastService {


    private final String TAG = getClass().getSimpleName();
    private SharedPreferences myDefaultPreferences;
    private SharedPreferences notificationPreferences;
    private NotificationManager mNotificationManager;

    public NotificationService() {

        super("com.hplasplas.task5.Services.NotificationService");
    }

    @Override
    public void onCreate() {

        super.onCreate();
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
        myDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationPreferences = this.getSharedPreferences(NOTIFICATIONS_PREFERENCES_FILE, MODE_PRIVATE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    @Override
    protected boolean handleIntent(Intent intent) {

        if (DEBUG) {
            Log.d(TAG, "handleIntent: ");
        }
        Intent nextIntent;
        boolean postBoot = false;
        boolean notifyGlobalEnabled = true;
        long timeCorrection;
        long sleepTimeInterval;
        long alarmIntervalToSet;
        long startPointTime;
        long previousAlarmInterval;
        long notificationInterval;
        int unReadNotificationsCounter;
        int countMessagesToShow;
        String message;

        if (intent.getBooleanExtra(CURRENT_NOTIFICATION_READ, false)) {
            if (DEBUG) {
                Log.d(TAG, "handleIntent: Clear unReadNotificationsCounter");
            }
            notificationPreferences.edit()
                    .putInt(UNREAD_NOTIFICATIONS_COUNTER, 0)
                    .commit();
            if (intent.getBooleanExtra(NEED_ACTIVITY_START, false)) {
                Intent startSettingIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
                startSettingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startSettingIntent);
            }
            return true;
        }

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            nextIntent = new Intent(this.getApplicationContext(), NotificationService.class);
            postBoot = true;
        } else {
            nextIntent = intent;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notifyGlobalEnabled = mNotificationManager.areNotificationsEnabled();
        }
        if (!notifyGlobalEnabled || !myDefaultPreferences.getBoolean(NOTIFICATIONS_ENABLED, false)) {
            if (DEBUG) {
                Log.d(TAG, "handleIntent: notifications is disabled write default values");
            }
            AlarmManagerUtil.cancelServiceAlarm(this.getApplicationContext(), nextIntent, ALARM_PENDING_INTENT_ID);
            notificationPreferences.edit()
                    .putLong(START_POINT_TIME, 0)
                    .putLong(PREVIOUS_ALARM_INTERVAL, 0)
                    .commit();
            return true;
        }

        message = myDefaultPreferences.getString(NOTIFICATIONS_TEXT, getResources().getString(R.string.default_notifications_text));
        notificationInterval = myDefaultPreferences.getInt(NOTIFICATIONS_INTERVAL, DEFAULT_NOTIFICATIONS_INTERVAL) * MLL_PER_MIN;

        if (notificationPreferences.getBoolean(NOT_INITIALIZED, true)) {
            if (DEBUG) {
                Log.d(TAG, "handleIntent: notificationPreferences not Initialized, initialise");
            }
            startPointTime = System.currentTimeMillis();
            unReadNotificationsCounter = 0;
            previousAlarmInterval = notificationInterval;
            notificationPreferences.edit()
                    .putBoolean(NOT_INITIALIZED, false)
                    .commit();
        } else {
            startPointTime = notificationPreferences.getLong(START_POINT_TIME, System.currentTimeMillis());
            if (startPointTime == 0) {
                startPointTime = System.currentTimeMillis();
            }
            unReadNotificationsCounter = notificationPreferences.getInt(UNREAD_NOTIFICATIONS_COUNTER, 0);
            previousAlarmInterval = notificationPreferences.getLong(PREVIOUS_ALARM_INTERVAL, 0);
            if (previousAlarmInterval == 0) {
                previousAlarmInterval = notificationInterval;
            }
        }
        sleepTimeInterval = System.currentTimeMillis() - startPointTime;
        if (sleepTimeInterval < 0) {
            sleepTimeInterval = 0;
        }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (mNotificationManager.getActiveNotifications().length == 0 && !postBoot) {
                unReadNotificationsCounter = 0;
            }
        }
        if (postBoot) {
            countMessagesToShow += unReadNotificationsCounter;
            unReadNotificationsCounter = 0;
        }

        showNotifications(message, unReadNotificationsCounter, countMessagesToShow);

        notificationPreferences.edit()
                .putLong(START_POINT_TIME, startPointTime)
                .putLong(PREVIOUS_ALARM_INTERVAL, previousAlarmInterval)
                .putInt(UNREAD_NOTIFICATIONS_COUNTER, unReadNotificationsCounter + countMessagesToShow)
                .commit();

        return true;
    }

    private void showNotifications(String defaultMessage, int StartNotificationNumber, int notificationToShow) {

        if (notificationToShow != 0) {
            int notificationCounter = StartNotificationNumber + notificationToShow;
            String expandedMessage = getResources().getString(R.string.notifications_extra_text);

            Intent startServiceIntent = new Intent(this.getApplicationContext(), NotificationService.class);
            startServiceIntent.putExtra(CURRENT_NOTIFICATION_READ, true);
            Intent startActivityIntent = new Intent(this.getApplicationContext(), NotificationService.class);
            startActivityIntent.putExtra(CURRENT_NOTIFICATION_READ, true);
            startActivityIntent.putExtra(NEED_ACTIVITY_START, true);
            PendingIntent pendingServiceIntent = PendingIntent.getService(this.getApplicationContext(), SERVICE_PENDING_INTENT_ID, startServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent pendingActivityIntent = PendingIntent.getService(this.getApplicationContext(), ACTIVITY_PENDING_INTENT_ID, startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

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
                mNotificationManager.notify(NOTIFY_TAG, NOTIFY_ID, mNotifyBuilder.build());
            }
        }
    }
}



