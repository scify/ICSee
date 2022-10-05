package gr.scify.icsee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

public class ICSeeSettingsActivity extends LocalizedActivity {

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

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        Activity activity;

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            activity = context instanceof Activity ? (Activity) context : null;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
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
        public void onPause() {
            super.onPause();
            Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(activity.getString(R.string.prefs_interface_language_key)) && isAdded()) {
                LocaleManager.updateLocale(getContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(key, ""));
                showAlertAndRestart();
            }
        }

        public void showAlertAndRestart() {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(R.string.preferences_updated);
            alert.setMessage(R.string.preferences_updated_body);
            alert.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                startActivity(new Intent(activity.getApplicationContext(), ICSeeStartActivity.class)
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