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

    public static void tutorialOn() {
        mEditor.putInt(KEY_TUTORIAL, 1);
        mEditor.apply();
    }

    public static void tutorialOff() {
        mEditor.putInt(KEY_TUTORIAL, 0);
        mEditor.apply();
    }

    public static int getTutorialState(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        //second parameter is default value
        tutorialState = mSharedPreferences.getInt(KEY_TUTORIAL, 1);
        Log.i(TAG, "state: " + tutorialState);
        return tutorialState;
    }

    public static void stopSound() {
            mediaPlayer.stop();
            Log.i(TAG, "Media player stopped");
    }

    public static void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public static void playSound(Context context, int soundID) {
        Log.i(TAG, "soundId: " + soundID);
        //Log.i(TAG, "state (sound): " + tutorialState);
        if(getTutorialState(ICSeeRealtimeActivity.mContext) == 1) {
            int sound = 0;
            switch (soundID) {
                case 1:
                    sound = R.raw.tutorial1;
                    break;
                case 2:
                    sound = R.raw.tutorial2;
                    break;
                case 3:
                    sound = R.raw.tutorial3;
                    break;
                case 4:
                    sound = R.raw.tutorial4;
                    break;
                case 5:
                    sound = R.raw.tutorial5;
                    break;
                default:
                    break;
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer = MediaPlayer.create(context, sound);
            mediaPlayer.start();
        }

    }
}
