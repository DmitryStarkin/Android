/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.reminders.setting;

import com.hplasplas.reminders.BuildConfig;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public final class Constants {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static final long INTERVAL_ACCURACY = 6000;

    public static final int SERVICE_PENDING_INTENT_ID = 1;
    public static final int ACTIVITY_PENDING_INTENT_ID = 2;
    public static final int ALARM_PENDING_INTENT_ID = 0;
    public static final int MLL_PER_MIN = 60000;
    public static final int DEFAULT_NOTIFICATIONS_INTERVAL = 1;

    public static final int NOTIFY_ID = 1;
    public static final String NOTIFY_TAG = "com.hplasplas.task5";

    public static final String NOTIFICATIONS_PREFERENCES_FILE = "notifyPref";

    public static final String CURRENT_NOTIFICATION_READ = "currentNotificationRead";
    public static final String NEED_ACTIVITY_START = "needStartActivity";
    public static final String START_POINT_TIME = "startPointTime";
    public static final String PREVIOUS_ALARM_INTERVAL = "previousAlarmInterval";
    public static final String NOT_INITIALIZED = "notInitialized";
    public static final String UNREAD_NOTIFICATIONS_COUNTER = "unReadNotificationsCounter";

    //Screen preferences names See file layout_values.xml
    public static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String NOTIFICATIONS_INTERVAL = "notification_interval";
    public static final String NOTIFICATIONS_TEXT = "notifications_text";

}
