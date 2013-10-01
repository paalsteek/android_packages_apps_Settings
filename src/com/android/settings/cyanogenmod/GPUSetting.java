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
public class GPUSetting extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String GPU_MAX_FREQ_PREF = "pref_gpu_max_freq";
    public static final String GPU_MAX_FREQ_FILE = "/proc/gpu/max_rate";

    public static final String SOB_PREF = "pref_gpu_max_freq_set_on_boot";

    private static final String TAG = "GPUSetting";

    private String mGPUMaxFreqFormat;

    private ListPreference mGPUMaxFreqPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGPUMaxFreqFormat = getString(R.string.gpu_max_freq_summary);
        String curGPUMaxFreq = null;

        addPreferencesFromResource(R.xml.gpu_settings);

        PreferenceScreen prefScreen = getPreferenceScreen();

        mGPUMaxFreqPref = (ListPreference) prefScreen.findPreference(GPU_MAX_FREQ_PREF);

        /*
         * SD Buffer size Some systems might not use SD Buffer
         */
        if (!Utils.fileExists(GPU_MAX_FREQ_FILE) ||
                (curGPUMaxFreq = Utils.fileReadOneLine(GPU_MAX_FREQ_FILE)) == null) {
            prefScreen.removePreference(mGPUMaxFreqPref);
        } else {
            // mGPUMaxFreqPref.setEntryValues(R.array.gpu_max_freq_values);
            // mGPUMaxFreqPref.setEntries( R.array.gpu_max_freq_entries);
            if (curGPUMaxFreq != null)
                mGPUMaxFreqPref.setValue(curGPUMaxFreq);
            mGPUMaxFreqPref.setSummary(String.format(mGPUMaxFreqFormat,
                    (Integer.valueOf(curGPUMaxFreq) / 1000000)));
            mGPUMaxFreqPref.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public void onResume() {
        String curGPUMaxFreq;

        super.onResume();

        if (Utils.fileExists(GPU_MAX_FREQ_FILE) &&
                (curGPUMaxFreq = Utils.fileReadOneLine(GPU_MAX_FREQ_FILE)) != null) {
            mGPUMaxFreqPref.setSummary(String.format(mGPUMaxFreqFormat,
                    (Integer.valueOf(curGPUMaxFreq) / 1000000)));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String fname = "";

        if (newValue != null) {
            if (preference == mGPUMaxFreqPref) {
                fname = GPU_MAX_FREQ_FILE;
            }

            if (Utils.fileWriteOneLine(fname, (String) newValue)) {
                if (preference == mGPUMaxFreqPref) {
                    mGPUMaxFreqPref.setSummary(String.format(mGPUMaxFreqFormat,
                            (Integer.valueOf(newValue.toString()) / 1000000)));
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
