package com.hplasplas.task5.Activitys;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.hplasplas.task5.R;
import com.hplasplas.task5.Services.NotificationService;

import static com.hplasplas.task5.Setting.Constants.DEBUG;


public class SettingsActivity extends PreferenceActivity {
    
    private final String TAG = getClass().getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }
    
    @Override
    protected void onStop() {
        
        super.onStop();
        if (DEBUG) {
            Log.d(TAG, "onStop: ");
        }
        Intent newIntent = new Intent(this.getApplicationContext(), NotificationService.class);
        startService(newIntent);
    }
    
    public static class MyPreferenceFragment extends PreferenceFragment {
        
        private final String TAG = getClass().getSimpleName();
        
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            
            if (DEBUG) {
                Log.d(TAG, "onCreate: ");
            }
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }
}
