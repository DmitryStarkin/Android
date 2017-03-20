package com.hplasplas.task5.receivers;

import android.util.Log;

import com.hplasplas.task5.services.NotificationService;
import com.starsoft.serviceutil.receivers.LongRunningReceiver;

import static com.hplasplas.task5.setting.Constants.DEBUG;

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
