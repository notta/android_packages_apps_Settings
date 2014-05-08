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

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.view.RotationPolicy;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class DisplayRotation extends SettingsPreferenceFragment {
    private static final String TAG = "DisplayRotation";

    private static final String KEY_ACCELEROMETER = "accelerometer";
    private static final String KEY_LOCKSCREEN_ROTATION = "lockscreen_rotation";

    private CheckBoxPreference mAccelerometer;

    private ContentObserver mAccelerometerRotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updateAccelerometerRotationCheckbox();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.display_rotation);

        PreferenceScreen prefSet = getPreferenceScreen();

        mAccelerometer = (CheckBoxPreference) findPreference(KEY_ACCELEROMETER);
        mAccelerometer.setPersistent(false);

        boolean hasRotationLock = getResources().getBoolean(
                com.android.internal.R.bool.config_hasRotationLockSwitch);

        if (hasRotationLock) {
            // Disable accelerometer checkbox, but leave others enabled
            mAccelerometer.setEnabled(false);
        }

        final CheckBoxPreference lockScreenRotation =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_ROTATION);
        boolean canRotateLockscreen = getResources().getBoolean(
                com.android.internal.R.bool.config_enableLockScreenRotation);

    }

    @Override
    public void onResume() {
        super.onResume();

        updateState();
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), true,
                mAccelerometerRotationObserver);
    }

    @Override
    public void onPause() {
        super.onPause();

        getContentResolver().unregisterContentObserver(mAccelerometerRotationObserver);
    }

    private void updateState() {
        updateAccelerometerRotationCheckbox();
    }

    private void updateAccelerometerRotationCheckbox() {
        mAccelerometer.setChecked(!RotationPolicy.isRotationLocked(getActivity()));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mAccelerometer) {
            RotationPolicy.setRotationLockForAccessibility(getActivity(),
                    !mAccelerometer.isChecked());
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
