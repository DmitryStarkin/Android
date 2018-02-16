/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * //www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an «AS IS» BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.starsoft.serviceutil.alarmutil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class AlarmManagerUtil {

    private static long setUpAlarm(AlarmManager alarmManager, PendingIntent pi, long timeInterval) {
        long curTime;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo((curTime = System.currentTimeMillis()) + timeInterval, pi);
            alarmManager.setAlarmClock(alarmClockInfo, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, (curTime = System.currentTimeMillis()) + timeInterval, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, (curTime = System.currentTimeMillis()) + timeInterval, pi);
        }
        return curTime;
    }

    public static synchronized boolean isServiceAlarmOn(Context context, Intent intent, int identifier) {

        PendingIntent pi = PendingIntent.getService(context, identifier, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public static synchronized boolean isBroadcastAlarmOn(Context context, Intent intent, int identifier) {

        PendingIntent pi = PendingIntent.getBroadcast(context, identifier, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public static synchronized long setBroadcastAlarm(Context context, Intent intent, long timeInterval, int identifier) {

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getBroadcast(context, identifier, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return setUpAlarm(am, pi, timeInterval);
    }

    public static synchronized long setServiceAlarm(Context context, Intent intent, long timeInterval, int identifier) {

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getService(context, identifier, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return setUpAlarm(am, pi, timeInterval);
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