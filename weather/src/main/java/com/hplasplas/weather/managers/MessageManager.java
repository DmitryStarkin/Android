/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of weather
 *
 *     weather is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    weather is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with weather  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hplasplas.weather.managers;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hplasplas.weather.R;
import com.hplasplas.weather.dialogs.MessageDialog;

/**
 * Created by StarkinDG on 24.04.2017.
 */

public class MessageManager {
    
    private final String MESSAGE_DIALOG_TAG = "dialogTag";
    private Context mAppContext;
    
    public MessageManager(Context context){
        mAppContext = context;
    }
    
    public void makeShortToast(String message) {
        
        Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT).show();
    }
    
    public void makeLongToast(String message) {
        
        Toast.makeText(mAppContext, message, Toast.LENGTH_LONG).show();
    }
    
    public void makeSnackbarMessage(View parent, String message, int duration) {
        
        Snackbar.make(parent, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.button_ok, v -> {})
                .setDuration(duration)
                .show();
    }
    
    public void makeDialogMessage(AppCompatActivity activity, String message, int icon){
    
        MessageDialog.newInstance(message, icon).show(activity.getSupportFragmentManager(), MESSAGE_DIALOG_TAG);
    }
}
