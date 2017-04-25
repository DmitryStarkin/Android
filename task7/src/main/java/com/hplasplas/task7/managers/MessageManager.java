package com.hplasplas.task7.managers;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hplasplas.task7.R;
import com.hplasplas.task7.dialogs.MessageDialog;

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
                .setAction(mAppContext.getResources().getString(R.string.button_ok), null)
                .setDuration(duration)
                .show();
    }
    
    public void makeDialogMessage(AppCompatActivity activity, String message, int icon){
    
        MessageDialog.newInstance(message, icon).show(activity.getSupportFragmentManager(), MESSAGE_DIALOG_TAG);
    }
}
