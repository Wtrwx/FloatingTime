package com.wtrwx.floatingtime;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_pref);
        final Preference floatingWindowPreference = (Preference) findPreference("floating_window");
        floatingWindowPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity activity= (MainActivity) getActivity();
                activity.startFloatingViewService();
                return true;
            }
        });
    }

}
