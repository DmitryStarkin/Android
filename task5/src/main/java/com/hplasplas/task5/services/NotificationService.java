package com.hplasplas.task5.services;

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

import com.hplasplas.task5.R;
import com.hplasplas.task5.activitys.SettingsActivity;
import com.starsoft.intentServiceUtil.Services.LongRunningBroadcastService;
import com.starsoft.intentServiceUtil.alarmUtil.AlarmManagerUtil;

import static com.hplasplas.task5.setting.Constants.ACTIVITY_PENDING_INTENT_ID;
import static com.hplasplas.task5.setting.Constants.ALARM_PENDING_INTENT_ID;
import static com.hplasplas.task5.setting.Constants.CURRENT_NOTIFICATION_READ;
import static com.hplasplas.task5.setting.Constants.DEBUG;
import static com.hplasplas.task5.setting.Constants.DEFAULT_NOTIFICATIONS_INTERVAL;
import static com.hplasplas.task5.setting.Constants.INTERVAL_ACCURACY;
import static com.hplasplas.task5.setting.Constants.MLL_PER_MIN;
import static com.hplasplas.task5.setting.Constants.NEED_ACTIVITY_START;
import static com.hplasplas.task5.setting.Constants.NOTIFICATIONS_ENABLED;
import static com.hplasplas.task5.setting.Constants.NOTIFICATIONS_INTERVAL;
import static com.hplasplas.task5.setting.Constants.NOTIFICATIONS_PREFERENCES_FILE;
import static com.hplasplas.task5.setting.Constants.NOTIFICATIONS_TEXT;
import static com.hplasplas.task5.setting.Constants.NOTIFY_ID;
import static com.hplasplas.task5.setting.Constants.NOTIFY_TAG;
import static com.hplasplas.task5.setting.Constants.NOT_INITIALIZED;
import static com.hplasplas.task5.setting.Constants.PREVIOUS_ALARM_INTERVAL;
import static com.hplasplas.task5.setting.Constants.SERVICE_PENDING_INTENT_ID;
import static com.hplasplas.task5.setting.Constants.START_POINT_TIME;
import static com.hplasplas.task5.setting.Constants.UNREAD_NOTIFICATIONS_COUNTER;

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
    
    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    @Override
    protected boolean handleIntent(Intent handledIntent) {
        
        if (DEBUG) {
            Log.d(TAG, "handleIntent: ");
        }
        
        long timeCorrection;
        long sleepTimeInterval;
        long alarmIntervalToSet;
        long startPointTime;
        long previousAlarmInterval;
        long notificationInterval;
        int unReadNotificationsCounter;
        int countMessagesToShow;
        String message;
        
        if(resetNotificationsCounter(handledIntent)){
            
            startActivityIfNeed(handledIntent);
            return true;
        }
        
        if (isNotifyDisabled()) {
    
            resetApplication(createNextIntent(handledIntent));
            return true;
        }
        
        message = mDefaultPreferences.getString(NOTIFICATIONS_TEXT, getResources().getString(R.string.default_notifications_text));
        notificationInterval = mDefaultPreferences.getInt(NOTIFICATIONS_INTERVAL, DEFAULT_NOTIFICATIONS_INTERVAL) * MLL_PER_MIN;
        
        if (mNotificationPreferences.getBoolean(NOT_INITIALIZED, true)) {
            if (DEBUG) {
                Log.d(TAG, "handleIntent: mNotificationPreferences not Initialized, initialise");
            }
            startPointTime = System.currentTimeMillis();
            unReadNotificationsCounter = 0;
            previousAlarmInterval = notificationInterval;
            mNotificationPreferences.edit()
                    .putBoolean(NOT_INITIALIZED, false)
                    .commit();
        } else {
            startPointTime = mNotificationPreferences.getLong(START_POINT_TIME, System.currentTimeMillis());
            if (startPointTime == 0) {
                startPointTime = System.currentTimeMillis();
            }
            unReadNotificationsCounter = mNotificationPreferences.getInt(UNREAD_NOTIFICATIONS_COUNTER, 0);
            previousAlarmInterval = mNotificationPreferences.getLong(PREVIOUS_ALARM_INTERVAL, 0);
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
            AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), createNextIntent(handledIntent), alarmIntervalToSet, ALARM_PENDING_INTENT_ID);
        } else {
            startPointTime = AlarmManagerUtil.setServiceAlarm(this.getApplicationContext(), createNextIntent(handledIntent), notificationInterval, ALARM_PENDING_INTENT_ID);
            if (notificationInterval < previousAlarmInterval) {
                countMessagesToShow++;
            }
        }
        previousAlarmInterval = notificationInterval;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (mNotificationManager.getActiveNotifications().length == 0 && !isPostBoot(handledIntent)) {
                unReadNotificationsCounter = 0;
            }
        }
        if (isPostBoot(handledIntent)) {
            countMessagesToShow += unReadNotificationsCounter;
            unReadNotificationsCounter = 0;
        }
        
        showNotifications(message, unReadNotificationsCounter, countMessagesToShow);
        
        mNotificationPreferences.edit()
                .putLong(START_POINT_TIME, startPointTime)
                .putLong(PREVIOUS_ALARM_INTERVAL, previousAlarmInterval)
                .putInt(UNREAD_NOTIFICATIONS_COUNTER, unReadNotificationsCounter + countMessagesToShow)
                .commit();
        
        return true;
    }
    
    
    
    @SuppressLint("ApplySharedPref")
    private boolean resetNotificationsCounter(Intent handledIntent){
        if (handledIntent.getBooleanExtra(CURRENT_NOTIFICATION_READ, false)) {
    
            mNotificationPreferences.edit()
                    .putInt(UNREAD_NOTIFICATIONS_COUNTER, 0)
                    .commit();
            return true;
        }
        return false;
    }
    
    private void startActivityIfNeed(Intent handledIntent){
        
        if (handledIntent.getBooleanExtra(NEED_ACTIVITY_START, false)) {
            Intent startSettingIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startSettingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startSettingIntent);
        }
    }
    
    private boolean isNotifyGlobalEnabled(){
        
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N || mNotificationManager.areNotificationsEnabled();
    }
    
    private boolean isNotifyDisabled(){
    
       return !isNotifyGlobalEnabled() || !mDefaultPreferences.getBoolean(NOTIFICATIONS_ENABLED, false);
    }
    
    @SuppressLint("ApplySharedPref")
    private void resetApplication(Intent nextIntent){
        
        AlarmManagerUtil.cancelServiceAlarm(this.getApplicationContext(), nextIntent, ALARM_PENDING_INTENT_ID);
        mNotificationPreferences.edit()
                .putLong(START_POINT_TIME, 0)
                .putLong(PREVIOUS_ALARM_INTERVAL, 0)
                .commit();
    }
    
    private  Intent createNextIntent(Intent handledIntent){
    
        if (handledIntent.getAction() != null && handledIntent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            return new Intent(this.getApplicationContext(), NotificationService.class);
        } else {
            return handledIntent;
        }
    }
    
    private boolean isPostBoot(Intent handledIntent){
        
        return handledIntent.getAction() != null && handledIntent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
    }
    
    private void showNotifications(String defaultMessage, int startNotificationNumber, int notificationToShow) {
        
        if (notificationToShow != 0) {
            int notificationCounter = startNotificationNumber + notificationToShow;
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
            
            for (int i = startNotificationNumber; i < notificationCounter; i++) {
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



