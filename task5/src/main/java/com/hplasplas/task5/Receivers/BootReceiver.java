package com.hplasplas.task5.Receivers;

import android.util.Log;

import com.hplasplas.task5.Services.NotificationService;
import com.starsoft.longRuningReceverUtil.Receivers.LongRunningReceiver;

import static com.hplasplas.task5.Setting.Constants.DEBUG;

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
