/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.reminders.services;

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

import com.hplasplas.reminders.R;
import com.hplasplas.reminders.activitys.SettingsActivity;
import com.starsoft.serviceutil.alarmutil.AlarmManagerUtil;
import com.starsoft.serviceutil.services.LongRunningBroadcastService;

import static com.hplasplas.reminders.setting.Constants.ACTIVITY_PENDING_INTENT_ID;
import static com.hplasplas.reminders.setting.Constants.ALARM_PENDING_INTENT_ID;
import static com.hplasplas.reminders.setting.Constants.CURRENT_NOTIFICATION_READ;
import static com.hplasplas.reminders.setting.Constants.DEBUG;
import static com.hplasplas.reminders.setting.Constants.DEFAULT_NOTIFICATIONS_INTERVAL;
import static com.hplasplas.reminders.setting.Constants.INTERVAL_ACCURACY;
import static com.hplasplas.reminders.setting.Constants.MLL_PER_MIN;
import static com.hplasplas.reminders.setting.Constants.NEED_ACTIVITY_START;
import static com.hplasplas.reminders.setting.Constants.NOTIFICATIONS_ENABLED;
import static com.hplasplas.reminders.setting.Constants.NOTIFICATIONS_INTERVAL;
import static com.hplasplas.reminders.setting.Constants.NOTIFICATIONS_PREFERENCES_FILE;
import static com.hplasplas.reminders.setting.Constants.NOTIFICATIONS_TEXT;
import static com.hplasplas.reminders.setting.Constants.NOTIFY_ID;
import static com.hplasplas.reminders.setting.Constants.NOTIFY_TAG;
import static com.hplasplas.reminders.setting.Constants.PREVIOUS_ALARM_INTERVAL;
import static com.hplasplas.reminders.setting.Constants.SERVICE_PENDING_INTENT_ID;
import static com.hplasplas.reminders.setting.Constants.START_POINT_TIME;
import static com.hplasplas.reminders.setting.Constants.UNREAD_NOTIFICATIONS_COUNTER;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class NotificationService extends LongRunningBroadcastService {
    
    private final String TAG = getClass().getSimpleName();
    private SharedPreferences mDefaultPreferences;
    private SharedPreferences mNotificationPreferences;
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
        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mNotificationPreferences = this.getSharedPreferences(NOTIFICATIONS_PREFERENCES_FILE, MODE_PRIVATE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    
    @Override
    protected boolean handleIntent(Intent handledIntent) {
        
        if (DEBUG) {
            Log.d(TAG, "handleIntent: ");
        }
        
        if (resetNotificationsCounter(handledIntent)) {
            
            startActivityIfNeed(handledIntent);
            return true;
        }
        
        if (isNotifyDisabled()) {
            
            resetApplication(createNextIntent(handledIntent));
            return true;
        }
        
        long currentNotifyInterval = mDefaultPreferences.getInt(NOTIFICATIONS_INTERVAL, DEFAULT_NOTIFICATIONS_INTERVAL) * MLL_PER_MIN;
        long startPointTime = getStartPointTime(mNotificationPreferences);
        long previousNotifyInterval = getPreviousNotifyInterval(mNotificationPreferences, currentNotifyInterval);
        long sleepTimeInterval = calculateSleepInterval(startPointTime);
        long timeCorrection = sleepTimeInterval % previousNotifyInterval;
        long alarmIntervalToSet = currentNotifyInterval - timeCorrection;
        int countMessagesToShow = calculateCountMessagesToShow(sleepTimeInterval, previousNotifyInterval);
        
        if ((alarmIntervalToSet - INTERVAL_ACCURACY) > 0) {
            startPointTime = startPointTime + countMessagesToShow * previousNotifyInterval;
            AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), createNextIntent(handledIntent), alarmIntervalToSet, ALARM_PENDING_INTENT_ID);
        } else {
            startPointTime = AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), createNextIntent(handledIntent), currentNotifyInterval, ALARM_PENDING_INTENT_ID);
            if (currentNotifyInterval < previousNotifyInterval) {
                countMessagesToShow++;
            }
        }
        
        int unReadNotificationsCounter = getUnReadNotifications(mNotificationPreferences, handledIntent);
        
        if (isPostBoot(handledIntent)) {
            countMessagesToShow += unReadNotificationsCounter;
            unReadNotificationsCounter = 0;
        }
        
        showNotify(unReadNotificationsCounter, countMessagesToShow);
        writeCurrentNotifyPreferences(mNotificationPreferences, startPointTime, currentNotifyInterval, unReadNotificationsCounter + countMessagesToShow);
        
        return true;
    }
    
    private int getUnReadNotifications(SharedPreferences notificationPreferences, Intent handledIntent) {
        
        int unReadNotificationsCounter = notificationPreferences.getInt(UNREAD_NOTIFICATIONS_COUNTER, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (mNotificationManager.getActiveNotifications().length == 0 && !isPostBoot(handledIntent)) {
                unReadNotificationsCounter = 0;
            }
        }
        return unReadNotificationsCounter;
    }
    
    @SuppressLint("ApplySharedPref")
    private void writeCurrentNotifyPreferences(SharedPreferences notificationPreferences, long startPointTime, long currentNotifyInterval, int unReadNotifications) {
        
        notificationPreferences.edit()
                .putLong(START_POINT_TIME, startPointTime)
                .putLong(PREVIOUS_ALARM_INTERVAL, currentNotifyInterval)
                .putInt(UNREAD_NOTIFICATIONS_COUNTER, unReadNotifications)
                .commit();
    }
    
    private long setNextNotifyTime(Intent handledIntent) {
        
        long currentNotifyInterval = mDefaultPreferences.getInt(NOTIFICATIONS_INTERVAL, DEFAULT_NOTIFICATIONS_INTERVAL) * MLL_PER_MIN;
        long startPointTime = getStartPointTime(mNotificationPreferences);
        long previousNotifyInterval = getPreviousNotifyInterval(mNotificationPreferences, currentNotifyInterval);
        long sleepTimeInterval = calculateSleepInterval(startPointTime);
        long timeCorrection = sleepTimeInterval % previousNotifyInterval;
        
        int countMessagesToShow = calculateCountMessagesToShow(sleepTimeInterval, previousNotifyInterval);
        
        long alarmIntervalToSet = currentNotifyInterval - timeCorrection;
        
        if ((alarmIntervalToSet - INTERVAL_ACCURACY) > 0) {
            startPointTime = startPointTime + calculateCountMessagesToShow(sleepTimeInterval, previousNotifyInterval) * previousNotifyInterval;
            AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), createNextIntent(handledIntent), alarmIntervalToSet, ALARM_PENDING_INTENT_ID);
        } else {
            startPointTime = AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), createNextIntent(handledIntent), currentNotifyInterval, ALARM_PENDING_INTENT_ID);
            if (currentNotifyInterval < previousNotifyInterval) {
                countMessagesToShow++;
            }
        }
        
        return startPointTime;
    }
    
    private long getStartPointTime(SharedPreferences notificationPreferences) {
        
        long startPointTime = notificationPreferences.getLong(START_POINT_TIME, 0);
        if (startPointTime == 0) {
            startPointTime = System.currentTimeMillis();
        }
        return startPointTime;
    }
    
    private long getPreviousNotifyInterval(SharedPreferences notificationPreferences, long currentNotifyInterval) {
        
        long previousNotifyInterval = notificationPreferences.getLong(PREVIOUS_ALARM_INTERVAL, 0);
        if (previousNotifyInterval == 0) {
            previousNotifyInterval = currentNotifyInterval;
        }
        return previousNotifyInterval;
    }
    
    private long calculateSleepInterval(long startPointTime) {
        
        long sleepTimeInterval = System.currentTimeMillis() - startPointTime;
        if (sleepTimeInterval < 0) {
            sleepTimeInterval = 0;
        }
        return sleepTimeInterval;
    }
    
    private int calculateCountMessagesToShow(long sleepTimeInterval, long previousNotifyInterval) {
        
        int countMessagesToShow = (int) (sleepTimeInterval / previousNotifyInterval);
        if ((sleepTimeInterval % previousNotifyInterval) > (previousNotifyInterval - INTERVAL_ACCURACY)) {
            countMessagesToShow++;
        }
        return countMessagesToShow;
    }
    
    @SuppressLint("ApplySharedPref")
    private boolean resetNotificationsCounter(Intent handledIntent) {
        
        if (handledIntent.getBooleanExtra(CURRENT_NOTIFICATION_READ, false)) {
            
            mNotificationPreferences.edit()
                    .putInt(UNREAD_NOTIFICATIONS_COUNTER, 0)
                    .commit();
            return true;
        }
        return false;
    }
    
    private void startActivityIfNeed(Intent handledIntent) {
        
        if (handledIntent.getBooleanExtra(NEED_ACTIVITY_START, false)) {
            Intent startSettingIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startSettingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startSettingIntent);
        }
    }
    
    private boolean isNotifyGlobalEnabled() {
        
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N || mNotificationManager.areNotificationsEnabled();
    }
    
    private boolean isNotifyDisabled() {
        
        return !isNotifyGlobalEnabled() || !mDefaultPreferences.getBoolean(NOTIFICATIONS_ENABLED, false);
    }
    
    @SuppressLint("ApplySharedPref")
    private void resetApplication(Intent nextIntent) {
        
        AlarmManagerUtil.cancelServiceAlarm(this.getApplicationContext(), nextIntent, ALARM_PENDING_INTENT_ID);
        mNotificationPreferences.edit()
                .putLong(START_POINT_TIME, 0)
                .putLong(PREVIOUS_ALARM_INTERVAL, 0)
                .commit();
    }
    
    private Intent createNextIntent(Intent handledIntent) {
        
        if (handledIntent.getAction() != null && handledIntent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            return new Intent(this.getApplicationContext(), NotificationService.class);
        } else {
            return handledIntent;
        }
    }
    
    private boolean isPostBoot(Intent handledIntent) {
        
        return handledIntent.getAction() != null && handledIntent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
    }
    
    private void showNotify(int startNotificationNumber, int notificationToShow) {
        
        if (notificationToShow != 0) {
            
            String message = mDefaultPreferences.getString(NOTIFICATIONS_TEXT, getResources().getString(R.string.default_notifications_text));
            String expandedMessage = getResources().getString(R.string.notifications_extra_text);
            
            Builder mNotifyBuilder = new Builder(this)
                    .setContentTitle(getResources().getString(R.string.notifications_title_text))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(createPendingIntent(ACTIVITY_PENDING_INTENT_ID))
                    .setDeleteIntent(createPendingIntent(SERVICE_PENDING_INTENT_ID))
                    .setAutoCancel(true);
            
            for (int i = startNotificationNumber, notificationCounter = startNotificationNumber + notificationToShow; i < notificationCounter; i++) {
                if (i > 10) {
                    mNotifyBuilder.setContentText(expandedMessage).setNumber(i + 1);
                } else {
                    mNotifyBuilder.setContentText(message).setNumber(i + 1);
                }
                mNotificationManager.notify(NOTIFY_TAG, NOTIFY_ID, mNotifyBuilder.build());
            }
        }
    }
    
    private PendingIntent createPendingIntent(int id) {
        
        Intent intent = new Intent(this.getApplicationContext(), NotificationService.class);
        intent.putExtra(CURRENT_NOTIFICATION_READ, true);
        if (id == ACTIVITY_PENDING_INTENT_ID) {
            intent.putExtra(NEED_ACTIVITY_START, true);
        }
        return PendingIntent.getService(this.getApplicationContext(), id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}



