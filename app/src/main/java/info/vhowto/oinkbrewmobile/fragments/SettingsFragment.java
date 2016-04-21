package info.vhowto.oinkbrewmobile.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.net.MalformedURLException;
import java.net.URL;

import info.vhowto.oinkbrewmobile.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    EditTextPreference apiServerUrl = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.application_settings);

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);

        apiServerUrl = (EditTextPreference) findPreference("pref_api_server_url");
        apiServerUrl.setSummary(sp.getString("pref_api_server_url", ""));
        apiServerUrl.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    URL url = new URL((String)newValue);
                    return true;
                }
                catch (MalformedURLException e) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.error_title_invalid_input);
                    builder.setMessage(R.string.error_msg_invalid_url);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    return false;
                }
            }
        });

        EditTextPreference editTextPref = (EditTextPreference) findPreference("pref_api_server_username");
        editTextPref.setSummary(sp.getString("pref_api_server_username", ""));
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference pref = findPreference(key);

        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;

            if (etp.getKey().equals("pref_api_server_url") && etp.getText().length() > 0 && etp.getText().endsWith("/")) {
                String url = etp.getText();
                etp.setText(url.substring(0, url.length()-1));
            }

            if (!etp.getKey().equals("pref_api_server_password") && !etp.getKey().equals("pref_api_allow_all_certs"))
                pref.setSummary(etp.getText());
        }
    }
}
