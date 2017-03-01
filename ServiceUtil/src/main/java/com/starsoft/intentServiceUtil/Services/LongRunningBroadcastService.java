package com.starsoft.intentServiceUtil.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;

import com.starsoft.intentServiceUtil.Managers.WakeLockManager;

/**
 * Created by StarkinDG on 26.02.2017.
 */

public abstract class LongRunningBroadcastService extends IntentService {

    public LongRunningBroadcastService(String name) {

        super(name);
    }

    protected abstract boolean handleIntent(Intent intent);

    @Override
    public void onCreate() {

        super.onCreate();
        WakeLockManager.create(this.getApplicationContext());
        WakeLockManager.registerAsClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        super.onStart(intent, startId);
        WakeLockManager.enterWakeLock();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        WakeLockManager.unRegisterAsClient();
    }

    @Override
    final protected void onHandleIntent(Intent intent) {

        try {
            Intent broadcastIntent = intent.getParcelableExtra("original_intent");
            if (broadcastIntent != null) {
                handleIntent(broadcastIntent);
            } else {
                handleIntent(intent);
            }
        } finally {
            WakeLockManager.leaveWakeLock();
        }
    }
}
