package gr.scify.icsee;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import java.util.HashMap;

public class ICSeeTutorial {
    private static final String PREFS_FILE = "gr.scify.icsee.preferences";
    private static final String KEY_TUTORIAL = "key_tutorial";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    protected static String TAG = ICSeeTutorial.class.getCanonicalName();
    public static final int Stutorial1 = R.raw.tutorial1;
    public static final int Stutorial2 = R.raw.tutorial2;
    public static final int Stutorial3 = R.raw.tutorial3;
    public static final int Stutorial4 = R.raw.tutorial4;
    public static final int Stutorial5 = R.raw.tutorial5;
    public static final int S6 = R.raw.pan14tonebeep;
    public static final int S7 = R.raw.camerashutterclick;
    public static final int S8 = R.raw.exit3;
    public static final int S9 = R.raw.auto_focus;
    private static SoundPool sp;                    // SoundPool to play the sounds
    private static HashMap<Integer, Integer> spMap; // Keeps the ID of each sound
    private static float volume = 1f;
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

    public static void initSounds(Context context) {
        // Init soundpool
        sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        spMap = new HashMap<Integer, Integer>();
        spMap.put(Stutorial1, sp.load(context, Stutorial1, 1));
        spMap.put(Stutorial2, sp.load(context, Stutorial2, 1));
        spMap.put(Stutorial3, sp.load(context, Stutorial3, 1));
        spMap.put(Stutorial4, sp.load(context, Stutorial4, 1));
        spMap.put(Stutorial5, sp.load(context, Stutorial5, 1));
        spMap.put(S6, sp.load(context, S6, 1));
        spMap.put(S7, sp.load(context, S7, 1));
        spMap.put(S8, sp.load(context, S8, 1));
        spMap.put(S9, sp.load(context, S9, 1));
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
        /*if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
        }*/
            switch (soundID) {
                case 1:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer = MediaPlayer.create(context, R.raw.tutorial1);
                    mediaPlayer.start();
                    break;
                case 2:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer = MediaPlayer.create(context, R.raw.tutorial2);
                    mediaPlayer.start();
                    break;
                case 3:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer = MediaPlayer.create(context, R.raw.tutorial3);
                    mediaPlayer.start();
                    break;
                case 4:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer = MediaPlayer.create(context, R.raw.tutorial4);
                    mediaPlayer.start();
                    break;
                case 5:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer = MediaPlayer.create(context, R.raw.tutorial5);
                    mediaPlayer.start();
                    break;
                default:
                    break;

            }
        }

    }
}
