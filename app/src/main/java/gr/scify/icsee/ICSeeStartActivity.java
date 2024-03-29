package gr.scify.icsee;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.android.volley.VolleyError;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import gr.scify.icsee.camera.ModifiedLoaderCallback;
import gr.scify.icsee.controllers.AnalyticsController;
import gr.scify.icsee.data.LoginRepository;
import gr.scify.icsee.data.StringVolleyCallback;
import gr.scify.icsee.login.LoginActivity;
import io.sentry.Sentry;

public class ICSeeStartActivity extends LocalizedActivity {
    protected Context mContext;
    public ModifiedLoaderCallback mOpenCVCallBack;
    public static ProgressDialog mDialog;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();
    protected ProgressBar mProgressBar;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    ActivityResultLauncher<Intent> activityResultLauncher;
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
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle bundle = new Bundle();
                        analyticsController.sendEvent(getApplicationContext(), "app_started", AnalyticsController.getCurrentLocale(getApplicationContext()).getLanguage(), bundle);
                    }
                });
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

    @Override
    protected void onResume() {
        super.onResume();
        this.checkSHAPESModeAndTokenAndShowLoginPage();
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

    private void checkSHAPESModeAndTokenAndShowLoginPage() {
        LoginRepository loginRepository = LoginRepository.getInstance();
        // check for auth token passed by external intent
        String token = getTokenFromExternalIntent();
        if (token != null && !token.isEmpty()) {
            loginRepository.storeToken(getApplicationContext(), token);
            return;
        }
        // check for shapes mode
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean shapesMode = preferences.getBoolean(getString(R.string.prefs_shapes_mode_key), false);
        if (shapesMode) {
            String storedToken = loginRepository.getStoredAuthToken(getApplicationContext());
            if (storedToken != null) {
                loginRepository.checkToken(storedToken, new StringVolleyCallback() {

                    @Override
                    public void onSuccess(String response) {
                    }

                    @Override
                    public void onError(VolleyError error) {
                        String body = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        }
                        Log.d(TAG, body);
                        loginRepository.deleteStoredUser(getApplicationContext());
                        goToLoginPage();
                    }
                });
            } else {
                goToLoginPage();
            }
        }
    }

    private void goToLoginPage() {
        mProgressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, LoginActivity.class);
        activityResultLauncher.launch(intent);
    }

    private String getTokenFromExternalIntent() {
        String toReturn = null;
        Intent intent = getIntent();
        if (intent != null) {
            String data = intent.getDataString();
            if (data != null && !data.isEmpty()) {
                Uri uri = Uri.parse(data);
                Set<String> args = uri.getQueryParameterNames();
                if (args.contains("token"))
                    toReturn = uri.getQueryParameter("token");
            }
        }
        return toReturn;
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
            Log.e("OpenCV", "Unable to load OpenCV! Trying the old way (OpenCV Manager app");
            // here we try to open the OpenCVManager app
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mOpenCVCallBack);
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

