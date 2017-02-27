package com.starsoft.longRuningReceverUtil.Recevers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.starsoft.longRuningReceverUtil.Managers.WakeLockManager;

/**
 * Created by StarkinDG on 26.02.2017.
 */

public abstract class LongRunningReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        WakeLockManager.create(context);
        startService(context, intent);
    }

    private void startService(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, getServiceClass());
        serviceIntent.putExtra("original_intent", intent);
        context.startService(serviceIntent);
    }

    /**
     * Override this method
     * to return an object of class,
     * which belongs to the class
     * nonSticky service.
     */
    public abstract Class getServiceClass();
}
