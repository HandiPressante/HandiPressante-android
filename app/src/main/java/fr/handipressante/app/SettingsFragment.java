package fr.handipressante.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Toast;


/**
 * Created by Nico on 19/10/2015.
 */
public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences mPrefs;

    @Override
    public void onStart(){
        super.onStart();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }

        //List Preferences TODO: needs to be final?,
        //
        final ListPreference buttonSize = (ListPreference) findPreference( "button_size");
        if(buttonSize != null)
        {
            buttonSize.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    int data = Integer.parseInt(value.toString());
                    //getting selected List item.
                    String text = getActivity().getResources().getStringArray(R.array.array_settings_size_entries)[data];
                    buttonSize.setSummary(text);
                    return true;
                }
            });
        }


        getPreferenceManager().findPreference("font_size").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivity(intent);
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferences(findPreference(key));
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory cat = (PreferenceCategory) p;
            for (int i = 0; i < cat.getPreferenceCount(); i++) {
                initSummary(cat.getPreference(i));
            }
        } else {
            updatePreferences(p);
        }
    }

    private void updatePreferences(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }

        if (p instanceof CheckBoxPreference) {
            CheckBoxPreference checkPref = (CheckBoxPreference) p;
            ((CheckBoxPreference) p).setChecked(checkPref.isChecked());
        }

        if (p instanceof SwitchPreference) {
            SwitchPreference switchPref = (SwitchPreference) p;
            ((SwitchPreference) p).setChecked(switchPref.isChecked());
        }
    }
}






