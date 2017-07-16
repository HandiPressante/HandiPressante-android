package fr.handipressante.app;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.Menu;


/**
 * Created by Nico on 19/10/2015.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setHasOptionsMenu(true);

        final ListPreference buttonSize = (ListPreference) findPreference("button_size");
        if(buttonSize != null)
        {
            buttonSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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

        getPreferenceManager().findPreference("color").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle) {
        super.onActivityCreated(savedInstanceBundle);

        getActivity().setTitle(R.string.menu_settings);
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.help).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}






