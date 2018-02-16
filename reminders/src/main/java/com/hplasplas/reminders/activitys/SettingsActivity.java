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
package com.hplasplas.reminders.activitys;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hplasplas.reminders.R;
import com.hplasplas.reminders.services.NotificationService;

import static com.hplasplas.reminders.setting.Constants.DEBUG;
import static com.hplasplas.reminders.setting.Constants.DEFAULT_NOTIFICATIONS_INTERVAL;
import static com.hplasplas.reminders.setting.Constants.NOTIFICATIONS_ENABLED;
import static com.hplasplas.reminders.setting.Constants.NOTIFICATIONS_INTERVAL;
import static com.hplasplas.reminders.setting.Constants.NOTIFICATIONS_TEXT;

public class SettingsActivity extends PreferenceActivity {
    
    private final String TAG = getClass().getSimpleName();
    private boolean mPreferencesChanged = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }
    
    @Override
    protected void onPause() {
        
        if (mPreferencesChanged) {
            Intent newIntent = new Intent(this.getApplicationContext(), NotificationService.class);
            startService(newIntent);
        }
        if (DEBUG) {
            Log.d(TAG, "onPause: ");
        }
        super.onPause();
    }
    
    public void setPreferencesChanged(boolean preferencesChanged) {
        
        this.mPreferencesChanged = preferencesChanged;
    }
    
    public boolean notifyGlobalDisabled() {
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (!mNotificationManager.areNotificationsEnabled()) {
                return true;
            }
        }
        return false;
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
            
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            findPreference(NOTIFICATIONS_INTERVAL).setSummary(String.valueOf(sharedPreferences.getInt(NOTIFICATIONS_INTERVAL, DEFAULT_NOTIFICATIONS_INTERVAL)));
            findPreference(NOTIFICATIONS_TEXT).setSummary(sharedPreferences.getString(NOTIFICATIONS_TEXT, getResources().getString(R.string.default_notifications_text)));
            if (((SettingsActivity) getActivity()).notifyGlobalDisabled()) {
                Preference notifyEnabled = findPreference(NOTIFICATIONS_ENABLED);
                notifyEnabled.setPersistent(false);
                notifyEnabled.setEnabled(false);
            }
            
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        
        @Override
        public void onDestroyView() {
            
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            
            super.onDestroyView();
        }
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            
            ((SettingsActivity) getActivity()).setPreferencesChanged(true);
            
            if (key.equals(NOTIFICATIONS_INTERVAL)) {
                findPreference(key).setSummary(String.valueOf(sharedPreferences.getInt(key, DEFAULT_NOTIFICATIONS_INTERVAL)));
            } else if (key.equals(NOTIFICATIONS_TEXT)) {
                findPreference(key).setSummary(sharedPreferences.getString(key, getResources().getString(R.string.default_notifications_text)));
            }
        }
    }
}
