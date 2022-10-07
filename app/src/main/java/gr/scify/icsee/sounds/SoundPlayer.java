package gr.scify.icsee.sounds;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

import gr.scify.icsee.R;

/**
 * How to add new sounds:
 * 1. Add a new public static final int variable for the sound in the beginning of the class
 * 2. Add it to the hashmap in initSounds()
 */
public class SoundPlayer {
    // Static sound variables (STEP 1)
    public static int S1 = R.raw.alienxxxbeep;
    public static int S2 = R.raw.next;
    public static int S3 = R.raw.keykrushermicrowavebeep;
    public static int S4 = R.raw.kodackbeepbeep;
    public static int S5 = R.raw.emptybellbeep;
    public static int S6 = R.raw.pan14tonebeep;
    public static int S7 = R.raw.camerashutterclick;
    public static int S8 = R.raw.exit3;
    public static int S9 = R.raw.auto_focus;
    public static int Stutorial = R.raw.welcome;


    private static SoundPool sp;                    // SoundPool to play the sounds
    private static HashMap<Integer, Integer> spMap; // Keeps the ID of each sound

    public static void initSounds(Context context) {
        // Init soundpool
        sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        S1 = R.raw.alienxxxbeep;
        S2 = R.raw.next;
        S3 = R.raw.keykrushermicrowavebeep;
        S4 = R.raw.kodackbeepbeep;
        S5 = R.raw.emptybellbeep;
        S6 = R.raw.pan14tonebeep;
        S7 = R.raw.camerashutterclick;
        S8 = R.raw.exit3;
        S9 = R.raw.auto_focus;
        Stutorial = R.raw.welcome;
        // Init hashmap (STEP 2)
        spMap = new HashMap<>();
        spMap.put(S1, sp.load(context, S1, 1));
        spMap.put(S2, sp.load(context, S2, 1));
        spMap.put(S3, sp.load(context, S3, 1));
        spMap.put(S4, sp.load(context, S4, 1));
        spMap.put(S5, sp.load(context, S5, 1));
        spMap.put(S6, sp.load(context, S6, 1));
        spMap.put(S7, sp.load(context, S7, 1));
        spMap.put(S8, sp.load(context, S8, 1));
        spMap.put(S9, sp.load(context, S9, 1));
        spMap.put(Stutorial, sp.load(context, Stutorial, 1));
    }

    public static void playSound(Context context, int soundID) {
        if (sp == null || spMap == null) {
            initSounds(context);
        } else {
            float volume = 1f;
            sp.play(spMap.get(soundID), volume, volume, 1, 0, 1f);
        }
    }
}
