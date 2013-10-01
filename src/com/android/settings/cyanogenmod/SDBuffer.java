/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

//
// SD Buffer Related Settings
//
public class SDBuffer extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String SDBUFFER_PREF = "pref_sd_buffer";
    public static final String SDBUFFER_FILE = "/sys/devices/virtual/bdi/179:0/read_ahead_kb";

    public static final String SOB_PREF = "pref_sd_buffer_set_on_boot";

    private static final String TAG = "SDBuffer";

    private String mSDBufferFormat;

    private ListPreference mSDBufferPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSDBufferFormat = getString(R.string.sd_read_buffer_summary);
        String currentSDBuffer = null;

        addPreferencesFromResource(R.xml.sdbuffer_settings);

        PreferenceScreen prefScreen = getPreferenceScreen();

        mSDBufferPref = (ListPreference) prefScreen.findPreference(SDBUFFER_PREF);

        /* SD Buffer size
        Some systems might not use SD Buffer */
        if (!Utils.fileExists(SDBUFFER_FILE) ||
            (currentSDBuffer = Utils.fileReadOneLine(SDBUFFER_FILE)) == null) {
            prefScreen.removePreference(mSDBufferPref);
        } else {
            mSDBufferPref.setEntryValues(R.array.sd_buffer_size_values);
            mSDBufferPref.setEntries( R.array.sd_buffer_size_entries);
            if (currentSDBuffer != null)
                mSDBufferPref.setValue(currentSDBuffer);
            mSDBufferPref.setSummary(String.format(mSDBufferFormat, currentSDBuffer));
            mSDBufferPref.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public void onResume() {
        String currentSDBuffer;

        super.onResume();

        if (Utils.fileExists(SDBUFFER_FILE) &&
            (currentSDBuffer = Utils.fileReadOneLine(SDBUFFER_FILE)) != null) {
                mSDBufferPref.setSummary(String.format(mSDBufferFormat, currentSDBuffer));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String fname = "";

        if (newValue != null) {
            if (preference == mSDBufferPref) {
                fname = SDBUFFER_FILE;
            }

            if (Utils.fileWriteOneLine(fname, (String) newValue)) {
                if (preference == mSDBufferPref) {
                    mSDBufferPref.setSummary(String.format(mSDBufferFormat, (String) newValue));
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
