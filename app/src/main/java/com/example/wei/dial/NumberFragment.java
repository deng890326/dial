package com.example.wei.dial;


import android.os.Bundle;
import android.app.Fragment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.prefs.Preferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class NumberFragment extends PreferenceFragment {

    public static int NUM_COUNT = 5;
    public static final String KEY_NUMBERS[] = {
            "pref_key_number_1",
            "pref_key_number_2",
            "pref_key_number_3",
            "pref_key_number_4",
            "pref_key_number_5",
    };

    private EditTextPreference mNumberPreferences[] = new EditTextPreference[NUM_COUNT];




    public NumberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setTitle(newValue.toString());
                return true;
            }
        };

        for (int i = 0; i < NUM_COUNT; i++) {
            mNumberPreferences[i] = (EditTextPreference) findPreference(KEY_NUMBERS[i]);
            mNumberPreferences[i].setTitle(mNumberPreferences[i].getText());
            mNumberPreferences[i].setOnPreferenceChangeListener(onPreferenceChangeListener);
        }



        return root;
    }
}
