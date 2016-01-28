package gr.scify.icsee;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

public class ICSeeTutorial {
    private static final String PREFS_FILE = "gr.scify.icsee.preferences";
    private static final String KEY_TUTORIAL = "key_tutorial";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    protected static String TAG = ICSeeTutorial.class.getCanonicalName();
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static int tutorialState;
    private static String lang;
    private static String countryCode;

    public static void tutorialOn() {
        mEditor.putInt(KEY_TUTORIAL, 1);
        mEditor.apply();
    }

    public static void tutorialOff() {
        mEditor.putInt(KEY_TUTORIAL, 0);
        mEditor.apply();
    }

    public static void setLanguage(String language, String country) {
        lang = language;
        countryCode = country;
    }

    public static int getTutorialState(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        //second parameter is default value
        tutorialState = mSharedPreferences.getInt(KEY_TUTORIAL, 1);
        //Log.i(TAG, "state: " + tutorialState);
        return tutorialState;
    }



    public static void stopSound() {
            mediaPlayer.stop();
            //Log.i(TAG, "Media player stopped");
    }

    public static void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public static void playTutorialReminder(Context context) {
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 0) {
            int soundId = 0;
            //Log.i(TAG, "lang: " + lang + " code: " + countryCode);
            if (lang.equals("el") || countryCode.equals("gr")) {
                soundId = R.raw.gr_tutorial_reminder;
            } else {
                soundId = R.raw.en_tutorial_reminder;
            }
            startPLayer(context, soundId);
        } else {
            playDragFingerTutorial(context);
        }
    }

    public static void playTutorialOn(Context context) {

        int soundId = 0;
        if (lang.equals("el") || countryCode.equals("gr")) {
            soundId = R.raw.gr_tutorial_on;
        } else {
            soundId = R.raw.en_tutorial_on;
        }
        startPLayer(context, soundId);
    }

    public static void playTutorialOff(Context context) {
        int soundId = 0;
        if(lang.equals("el") || countryCode.equals("gr")) {
            soundId = R.raw.gr_tutorial_off;
        } else {
            soundId = R.raw.en_tutorial_off;
        }
        startPLayer(context, soundId);
    }

    public static void playAdjustZoom(Context context) {
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 1) {
            int soundId = 0;
            if (lang.equals("el") || countryCode.equals("gr")) {
                soundId = R.raw.gr_adjust_zoom;
            } else {
                soundId = R.raw.en_adjust_zoom;
            }
            startPLayer(context, soundId);
        }
    }

    public static void playTakePictureReminder(Context context) {
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 1) {
            int soundId = 0;
            if (lang.equals("el") || countryCode.equals("gr")) {
                soundId = R.raw.gr_take_picure;
            } else {
                soundId = R.raw.en_take_picture;
            }
            startPLayer(context, soundId);
        }
    }

    public static void playChangedFilter(Context context) {
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 1) {
            int soundId = 0;
            if (lang.equals("el") || countryCode.equals("gr")) {
                soundId = R.raw.gr_changed_filter;
            } else {
                soundId = R.raw.en_changed_filter;
            }
            startPLayer(context, soundId);
        }
    }

    public static void playDragFingerTutorial(Context context) {
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 1) {
            int soundId = 0;
            if (lang.equals("el") || countryCode.equals("gr")) {
                soundId = R.raw.gr_drag_finger_tutorial;
            } else {
                soundId = R.raw.en_drag_finger;
            }
            startPLayer(context, soundId);
        }
    }

    public static void playNoFiltersLeft(Context context) {
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 1) {
            int soundId = 0;
            if (lang.equals("el") || countryCode.equals("gr")) {
                soundId = R.raw.gr_no_filters_left;
            } else {
                soundId = R.raw.en_no_filters_left;
            }
            startPLayer(context, soundId);
        }
    }

    public static void playNoFiltersRight(Context context) {
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 1) {
            int soundId = 0;
            if (lang.equals("el") || countryCode.equals("gr")) {
                soundId = R.raw.gr_no_filters_right;
            } else {
                soundId = R.raw.en_no_filters_right;
            }
            startPLayer(context, soundId);
        }
    }

    public static void playAutoFocus(Context context) {
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 1) {
            int soundId = 0;
            if (lang.equals("el") || countryCode.equals("gr")) {
                soundId = R.raw.gr_auto_focus;
            } else {
                soundId = R.raw.en_auto_focus;
            }
            startPLayer(context, soundId);
        }
    }

    public static void startPLayer(Context context, int soundId) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(context, soundId);
        mediaPlayer.start();
    }
}
