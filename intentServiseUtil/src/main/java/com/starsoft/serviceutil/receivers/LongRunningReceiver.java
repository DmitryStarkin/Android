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

package com.starsoft.serviceutil.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.starsoft.serviceutil.managers.WakeLockManager;

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
