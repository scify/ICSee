package gr.scify.icsee;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

public class ICSeeSettingsActivity extends AppCompatActivity {
    public static final String PREFS_FILE = "gr.scify.icsee.preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setAppLocale(getBaseContext());
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            PreferenceManager manager = getPreferenceManager();
            manager.setSharedPreferencesName(PREFS_FILE);
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference pref1 = findPreference("app_version");
            try {
                assert pref1 != null;
                pref1.setSummary(getAppVersion());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            sharedPreferences.getBoolean(requireActivity().getString(R.string.prefs_shapes_mode_key), false);
            if (key.equals(requireActivity().getString(R.string.prefs_interface_language_key)))
                LocaleManager.updateLocale(requireActivity().getApplicationContext(), sharedPreferences.getString(requireActivity().getString(R.string.prefs_interface_language_key), null));
            showAlertAndRestart();
        }

        public void showAlertAndRestart() {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(R.string.preferences_updated);
            alert.setMessage(R.string.preferences_updated_body);
            alert.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                startActivity(new Intent(requireActivity().getBaseContext(), ICSeeStartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            });
            alert.show();
        }

        public String getAppVersion() throws PackageManager.NameNotFoundException {
            return BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE;
        }
    }
}