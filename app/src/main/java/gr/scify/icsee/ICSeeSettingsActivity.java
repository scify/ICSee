package gr.scify.icsee;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;
import java.util.Objects;

public class ICSeeSettingsActivity extends LocalizedActivity {
    static final String SHOULD_RESTART_APP_KEY = "should_restart_app";
    boolean shouldRestartApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SHOULD_RESTART_APP_KEY, shouldRestartApp);
        super.onSaveInstanceState(savedInstanceState);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference langPreference = findPreference(requireContext().getString(R.string.prefs_interface_language_key));
            String lang = LocaleManager.getPersistedLocale(requireActivity().getApplicationContext());
            int valueIndex = 0;
            if (lang.equals("el")) {
                valueIndex = 1;
            }
            Objects.requireNonNull(langPreference).setValueIndex(valueIndex);
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
        public void onPause() {
            super.onPause();
            Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(requireActivity().getString(R.string.prefs_interface_language_key)) && isAdded()) {
                String lang = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(key, "");
                LocaleManager.updateLocale(requireActivity(), lang);
                showAlertAndRestart(lang);
            }
        }

        public void showAlertAndRestart(String lang) {
            Configuration conf = getResources().getConfiguration();
            conf.locale = new Locale(lang);
            DisplayMetrics metrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Resources resources = new Resources(requireActivity().getAssets(), metrics, conf);
            String title = resources.getString(R.string.preferences_updated);
            String message = resources.getString(R.string.preferences_updated_body);
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(title);
            alert.setMessage(message);
            alert.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                startActivity(new Intent(requireActivity(), ICSeeStartActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                );
            });
            alert.show();
        }

        public String getAppVersion() throws PackageManager.NameNotFoundException {
            return BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE;
        }
    }
}