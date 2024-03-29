package gr.scify.icsee;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.preference.PreferenceManager;

import java.util.Locale;

import gr.scify.icsee.sounds.SoundPlayer;

public class LocaleManager {

    public static Context onAttach(Context context) {
        String locale = getPersistedLocale(context);
        return updateLocale(context, locale);
    }

    public static String getPersistedLocale(Context context) {
        String defaultSystemLang = Locale.getDefault().getLanguage();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.prefs_interface_language_key), defaultSystemLang);
    }

    /**
     * Set the app's locale to the one specified by the given String.
     *
     * @param context    the given context (Activity)
     * @param localeSpec a locale specification as used for Android resources (NOTE: does not
     *                   support country and variant codes so far); the special string "system" sets
     *                   the locale to the locale specified in system settings
     * @return the updated context (Activity)
     */
    public static Context updateLocale(Context context, String localeSpec) {
        Locale locale;
        if (localeSpec.equals("system")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            } else {
                locale = Resources.getSystem().getConfiguration().locale;
            }
        } else {
            locale = new Locale(localeSpec);
        }
        Locale.setDefault(locale);
        Context updatedContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updatedContext = updateResources(context, locale);
        } else {
            updatedContext = updateResourcesLegacy(context, locale);
        }
        SoundPlayer.initSounds(updatedContext);
        return updatedContext;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    private static Context updateResourcesLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}
