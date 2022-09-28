package gr.scify.icsee;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

import gr.scify.icsee.sounds.SoundPlayer;

public class LocaleManager {

    public static void setAppLocale(Context ctx) {
        // check if another language is set in preferences
        SharedPreferences preferences = ctx.getSharedPreferences(ICSeeSettingsActivity.PREFS_FILE, Context.MODE_PRIVATE);
        String prefLocale = preferences.getString(ctx.getString(R.string.prefs_interface_language_key), Locale.getDefault().getLanguage());
        updateLocale(ctx, prefLocale);
    }

    public static void updateLocale(Context ctx, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        }
        ctx.getResources().updateConfiguration(config,
                ctx.getResources().getDisplayMetrics());
        SoundPlayer.initSounds(ctx);
    }
}
