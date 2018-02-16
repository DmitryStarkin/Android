/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of reminders
 *
 *     reminders is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    reminders is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with reminders  If not, see <http://www.gnu.org/licenses/>.
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
