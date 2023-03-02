package gr.scify.icsee;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import androidx.preference.PreferenceManager;

public class ICSeeTutorial {

    private static final String KEY_TUTORIAL = "key_tutorial";
    private static SharedPreferences.Editor mEditor;
    protected static String TAG = ICSeeTutorial.class.getCanonicalName();
   // private static MediaPlayer mediaPlayer;

    public static void tutorialOn() {
        mEditor.putInt(KEY_TUTORIAL, 1);
        mEditor.apply();
    }

    public static void tutorialOff() {
        mEditor.putInt(KEY_TUTORIAL, 0);
        mEditor.apply();
    }

    public static int getTutorialState(Context context) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mSharedPreferences.edit();
        return mSharedPreferences.getInt(KEY_TUTORIAL, 1);
    }

    public static void stopSound() {
//        if (mediaPlayer != null)
//            mediaPlayer.stop();
    }

    public static void playTutorialReminder(Context context) {
        if (getTutorialState(context) == 0) {
            startPLayer(context, R.raw.tutorial_reminder);
        } else {
            playDragFingerTutorial(context);
        }
    }

    public static void playWelcome(Context context) {
        startPLayer(context, R.raw.welcome);
    }

    public static void playTutorialOn(Context context) {
        startPLayer(context, R.raw.tutorial_on);
    }

    public static void playTutorialOff(Context context) {
        startPLayer(context, R.raw.tutorial_off);
    }

    public static void playAdjustZoom(Context context) {
        if (getTutorialState(context) == 1) {
            startPLayer(context, R.raw.adjust_zoom);
        }
    }

    public static void playTakePictureReminder(Context context) {
        if (getTutorialState(context) == 1) {
            startPLayer(context, R.raw.take_picture);
        }
    }

    public static void playChangedFilter(Context context) {
        if (getTutorialState(context) == 1) {
            startPLayer(context, R.raw.changed_filter);
        }
    }

    public static void playDragFingerTutorial(Context context) {
        if (getTutorialState(context) == 1) {
            startPLayer(context, R.raw.drag_finger);
        }
    }

    public static void playNoFiltersLeft(Context context) {
        if (getTutorialState(context) == 1) {
            startPLayer(context, R.raw.no_filters_left);
        }
    }

    public static void playNoFiltersRight(Context context) {
        if (getTutorialState(context) == 1) {
            startPLayer(context, R.raw.no_filters_right);
        }
    }

    public static void playAutoFocus(Context context) {
        if (getTutorialState(context) == 1) {
            startPLayer(context, R.raw.auto_focus_tutorial);
        }
    }

    public static void startPLayer(Context context, int soundId) {
//        if (mediaPlayer == null)
//            mediaPlayer = new MediaPlayer();
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//        }
//        mediaPlayer = MediaPlayer.create(context, soundId);
//        mediaPlayer.start();
    }
}
