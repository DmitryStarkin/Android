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

package com.starsoft.serviceutil.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;

import com.starsoft.serviceutil.managers.WakeLockManager;

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
