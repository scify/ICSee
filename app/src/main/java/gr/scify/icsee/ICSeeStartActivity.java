package gr.scify.icsee;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import gr.scify.icsee.camera.ModifiedLoaderCallback;
import gr.scify.icsee.sounds.SoundPlayer;

public class ICSeeStartActivity extends Activity {
    protected Context mContext;
    public static ModifiedLoaderCallback mOpenCVCallBack;
    public static ProgressDialog mDialog;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();
    Button mExitButton;
    ProgressBar mProgressBar;
    static MediaPlayer mp = new MediaPlayer();
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        mContext = this;
        this.checkForIncomingData();
        this.checkForRuntimeCameraPermission();

        // Initialize sounds here, so they should have loaded when the camera view starts
        SoundPlayer.initSounds(this.getApplicationContext());
        this.initAudioManager();
        this.initScreenComponents();
        initOpenCV();
    }


    private void checkForIncomingData() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.i(TAG, "ABOUT TO READ INCOMING DATA");
        Log.i(TAG, "action = " + action);
        Log.i(TAG, "type = " + type);
        String data = intent.getDataString();
        Log.i(TAG, "data = " + data);
    }

    private void checkForRuntimeCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
        }
    }

    protected void initAudioManager() {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 3,
                0);
    }

    private void initScreenComponents() {
        mExitButton = findViewById(R.id.exitButton);
        mProgressBar = findViewById(R.id.progressBar);
        mExitButton.setOnLongClickListener(v -> {
            Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(250);
            mOpenCVCallBack.stopTutorial();
            new AsyncProgressCheck(mDialog, mOpenCVCallBack, ICSeeStartActivity.this).execute();
            return false;
        });

//        TextView t2 = findViewById(R.id.privacy_policy_link);
//        t2.setText(Html.fromHtml(
//                "<a href=\"https://www.scify.gr/site/el/impact-areas/165-icsee/438-icsee-privacy-policy\">Privacy policy</a>"));
//        t2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected void onPause() {
        super.onPause();
        mOpenCVCallBack.stopTutorial();
    }

    protected void initOpenCV() {
        mOpenCVCallBack = new ModifiedLoaderCallback(mContext, mProgressBar, mp, mDialog, ICSeeStartActivity.this);
        Log.i(TAG, "mOpenCVCallBack.hasManagerConnected: " + mOpenCVCallBack.hasManagerConnected);
        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Unable to load OpenCV!");
            // here we try to open the OpenCVManager app
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mOpenCVCallBack);
        } else {
            Log.d("OpenCV", "OpenCV loaded Successfully!");
            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void showErrorMessage(String sMsg, boolean bShouldClose) {
        // Hide progress bar
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        // Set errorMessage text
        TextView tvMsg = (TextView) findViewById(R.id.errorMessage);
        tvMsg.setText(sMsg);

        // Show errorMessage text
        tvMsg.setVisibility(View.VISIBLE);

        // Show button
        Button bExitBtn = (Button) findViewById(R.id.exitButton);
        bExitBtn.setVisibility(View.VISIBLE);

        // If bShouldClose
        if (bShouldClose) {
            bExitBtn.setOnClickListener(v -> {
                finish();
            });
        }
    }

}

