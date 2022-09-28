package gr.scify.icsee;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import gr.scify.icsee.camera.ModifiedLoaderCallback;

public class ICSeeStartActivity extends Activity {
    protected Context mContext;
    public static ModifiedLoaderCallback mOpenCVCallBack;
    public static ProgressDialog mDialog;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();
    protected ProgressBar mProgressBar;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setAppLocale(getBaseContext());
        setContentView(R.layout.start_activity);
        mContext = this;
        this.checkForIncomingData();
        this.checkForRuntimeCameraPermission();
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

    private void initScreenComponents() {
        Button mExitButton = findViewById(R.id.exitButton);
        mProgressBar = findViewById(R.id.progressBar);
        mExitButton.setOnLongClickListener(v -> {
            Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(250);
            ICSeeTutorial.stopSound();
            new AsyncProgressCheck(mDialog, mOpenCVCallBack, ICSeeStartActivity.this).execute();
            return false;
        });
        Button settingsBtn = findViewById(R.id.settings_btn);

        settingsBtn.setOnClickListener(v -> {
            // opening a new intent to open settings activity.
            Intent i = new Intent(getApplicationContext(), ICSeeSettingsActivity.class);
            startActivity(i);
        });
    }

    private void checkSHAPESModeAndTokenAndContinue() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }

    protected void onPause() {
        super.onPause();
        ICSeeTutorial.stopSound();
    }

    protected void initOpenCV() {
        mOpenCVCallBack = new ModifiedLoaderCallback(mContext, mProgressBar, mDialog, ICSeeStartActivity.this);
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
        ProgressBar pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        // Set errorMessage text
        TextView tvMsg = findViewById(R.id.errorMessage);
        tvMsg.setText(sMsg);

        // Show errorMessage text
        tvMsg.setVisibility(View.VISIBLE);

        // Show button
        Button bExitBtn = findViewById(R.id.exitButton);
        bExitBtn.setVisibility(View.VISIBLE);

        // If bShouldClose
        if (bShouldClose) {
            bExitBtn.setOnClickListener(v -> finish());
        }
    }

}

