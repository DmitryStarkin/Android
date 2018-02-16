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
package com.hplasplas.cam_capture.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hplasplas.cam_capture.loaders.BitmapInThreadLoader;
import com.hplasplas.cam_capture.managers.CollapsedElementsManager;

import static com.hplasplas.cam_capture.setting.Constants.MESSAGE_BITMAP_LOAD;
import static com.hplasplas.cam_capture.setting.Constants.MESSAGE_PANEL_MUST_HIDE;

/**
 * Created by StarkinDG on 24.03.2017.
 */

public class MainHandler extends Handler {
    
    private static MainHandler instance = null;
    
    private MainHandler() {
        
        super(Looper.getMainLooper());
    }
    
    public static synchronized MainHandler getInstance() {
        
        if (instance == null) {
            instance = new MainHandler();
        }
        return instance;
    }
    
    @Override
    public void handleMessage(Message msg) {
        
        if (msg.what == MESSAGE_BITMAP_LOAD) {
            ((BitmapInThreadLoader) msg.obj).onPostBitmapLoad();
        } else if (msg.what == MESSAGE_PANEL_MUST_HIDE) {
            ((CollapsedElementsManager) msg.obj).hideBottomPanel();
        }
    }
}

