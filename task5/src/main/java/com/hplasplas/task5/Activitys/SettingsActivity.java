package com.hplasplas.task5.Activitys;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hplasplas.task5.R;
import com.hplasplas.task5.Services.NotificationService;

import static com.hplasplas.task5.Setting.Constants.DEBUG;


public class SettingsActivity extends PreferenceActivity {
    
    private final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        Intent intent = getIntent();

        if (intent != null) {
            if (DEBUG) {
                Log.d(TAG, "onCreate: currentNotificationRead " + intent.getBooleanExtra("currentNotificationRead", false));
            }
            if (intent.getBooleanExtra("currentNotificationRead", false)) {
                Intent startServiceIntent = new Intent(this.getApplicationContext(), NotificationService.class);
                startServiceIntent.putExtra("currentNotificationRead", true);
                startService(startServiceIntent);
            }
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    protected void onStop() {

        Intent newIntent = new Intent(this.getApplicationContext(), NotificationService.class);
        startService(newIntent);
        if (DEBUG) {
            Log.d(TAG, "onStop: ");
        }
        super.onStop();
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
