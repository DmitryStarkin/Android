/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.reminders.receivers;

import android.util.Log;

import com.hplasplas.reminders.services.NotificationService;
import com.starsoft.serviceutil.receivers.LongRunningReceiver;

import static com.hplasplas.reminders.setting.Constants.DEBUG;

/**
 * Created by StarkinDG on 27.02.2017.
 */

public class BootReceiver extends LongRunningReceiver {

    private final String TAG = getClass().getSimpleName();

    @Override
    public Class getServiceClass() {

        if (DEBUG) {
            Log.d(TAG, "getServiceClass: ");
        }
        return NotificationService.class;
    }
}
