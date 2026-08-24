package gr.scify.icsee;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.opencv.android.OpenCVLoader;

import gr.scify.icsee.camera.ModifiedLoaderCallback;
import gr.scify.icsee.controllers.AnalyticsController;

public class ICSeeStartActivity extends LocalizedActivity {
    protected Context mContext;
    public ModifiedLoaderCallback mOpenCVCallBack;
    public static ProgressDialog mDialog;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();
    protected ProgressBar mProgressBar;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    protected AnalyticsController analyticsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        mContext = this;
        analyticsController = AnalyticsController.getInstance();
        this.initScreenComponents();
        if (cameraRuntimePermissionGranted()) {
            initOpenCV();
        } else {
            this.checkForRuntimeCameraPermission();
        }
        Bundle bundle = new Bundle();
        analyticsController.sendEvent(getApplicationContext(), "app_started", AnalyticsController.getCurrentLocale(getApplicationContext()).getLanguage(), bundle);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (cameraRuntimePermissionGranted()) {
            initOpenCV();
        }
    }

    private void checkForRuntimeCameraPermission() {
        if (!cameraRuntimePermissionGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
        }
    }

    private boolean cameraRuntimePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void initScreenComponents() {
        Button mExitButton = findViewById(R.id.exitButton);
        mProgressBar = findViewById(R.id.progressBar);
        mExitButton.setOnLongClickListener(v -> {
            Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(250);
            ICSeeTutorial.stopSound();
            new AsyncProgressCheck(mDialog, mOpenCVCallBack, this).execute();
            return false;
        });
        Button settingsBtn = findViewById(R.id.settings_btn);

        settingsBtn.setOnClickListener(v -> {
            // opening a new intent to open settings activity.
            Intent i = new Intent(getApplicationContext(), ICSeeSettingsActivity.class);
            startActivity(i);
        });
    }

    protected void onPause() {
        super.onPause();
        ICSeeTutorial.stopSound();
    }

    protected void initOpenCV() {
        mOpenCVCallBack = new ModifiedLoaderCallback(mContext, mProgressBar, mDialog, ICSeeStartActivity.this);
        Log.i(TAG, "Trying to load OpenCV library");
        boolean canLoad = OpenCVLoader.initDebug();
        if (!canLoad) {
            Log.e("OpenCV", "Unable to load OpenCV!");
        } else {
            Log.d("OpenCV", "OpenCV loaded Successfully!");
            mOpenCVCallBack.onManagerConnected(ModifiedLoaderCallback.SUCCESS);
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

