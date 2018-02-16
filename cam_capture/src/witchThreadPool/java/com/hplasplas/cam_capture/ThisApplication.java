/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of cam_capture
 *
 *     cam_capture is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    cam_capture is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cam_capture  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hplasplas.cam_capture;

import android.app.Application;
import android.content.Context;

/**
 * Created by StarkinDG on 26.03.2017.
 */

public class ThisApplication extends Application {
    
    private static ThisApplication instance;
    
    public static synchronized ThisApplication getInstance() {
        return instance;
    }
    
    public static synchronized Context getMainContext() {
        return instance.getApplicationContext();
    }
    
    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
