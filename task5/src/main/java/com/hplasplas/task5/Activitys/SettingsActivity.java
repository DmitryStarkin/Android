package com.hplasplas.task5.Activitys;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hplasplas.task5.R;
import com.hplasplas.task5.Services.NotificationService;

import static com.hplasplas.task5.Setting.Constants.DEBUG;
import static com.hplasplas.task5.Setting.Constants.NOTIFICATIONS_INTERVAL;
import static com.hplasplas.task5.Setting.Constants.NOTIFICATIONS_TEXT;


public class SettingsActivity extends PreferenceActivity {
    
    private final String TAG = getClass().getSimpleName();
    private boolean preferencesChanged = false;

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
    protected void onPause() {

        if (preferencesChanged) {
            Intent newIntent = new Intent(this.getApplicationContext(), NotificationService.class);
            startService(newIntent);
        }
        if (DEBUG) {
            Log.d(TAG, "onPause: ");
        }
        super.onPause();
    }

    public void setPreferencesChanged(boolean preferencesChanged) {

        this.preferencesChanged = preferencesChanged;
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        
        private final String TAG = getClass().getSimpleName();


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            
            if (DEBUG) {
                Log.d(TAG, "onCreate: ");
            }
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);


            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            findPreference(NOTIFICATIONS_INTERVAL).setSummary(String.valueOf(sharedPreferences.getInt(NOTIFICATIONS_INTERVAL, 30)));
            findPreference(NOTIFICATIONS_TEXT).setSummary(sharedPreferences.getString(NOTIFICATIONS_TEXT, getResources().getString(R.string.default_notifications_text)));
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onDestroyView() {

            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);

            super.onDestroyView();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            ((SettingsActivity) getActivity()).setPreferencesChanged(true);

            if (key.equals(NOTIFICATIONS_INTERVAL)) {
                findPreference(key).setSummary(String.valueOf(sharedPreferences.getInt(key, 30)));
            } else if (key.equals(NOTIFICATIONS_TEXT)) {
                findPreference(key).setSummary(sharedPreferences.getString(key, getResources().getString(R.string.default_notifications_text)));
            }
        }
    }
}
