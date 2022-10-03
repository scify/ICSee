package gr.scify.icsee;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import gr.scify.icsee.camera.ImageView;
import gr.scify.icsee.camera.RealtimeFilterView;
import gr.scify.icsee.controllers.AnalyticsController;
import gr.scify.icsee.filters.opencv.matfilters.MatAdaptiveThresholding;
import gr.scify.icsee.filters.opencv.matfilters.MatBinarizationFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatBlackYellowFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatBlueYellowFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatBlueYellowInvertedFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatNegative;
import gr.scify.icsee.filters.opencv.matfilters.MatWhiteRedFilter;
import gr.scify.icsee.sounds.SoundPlayer;

public class ICSeeRealtimeActivity extends Activity implements OnGesturePerformedListener {
    private GestureLibrary gestureLib;
    public RealtimeFilterView mView = null;
    public static Context mContext;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();
    private static String currentFilter = "";
    final Handler mHandlerTutorial = new Handler();
    private static boolean movementTutorial = false;
    private static int movementCounter = 0;
    protected static AnalyticsController analyticsController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        analyticsController = AnalyticsController.getInstance();
        // Allow long clicks
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.custom, null);
        gestureOverlayView.addView(inflate);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
            finish();
        }
        // Init Handler
        setContentView(R.layout.activity_main);
        inflate.setOnClickListener(arg0 -> {
            // Play start focus sound
            SoundPlayer.playSound(arg0.getContext(), SoundPlayer.S9);
            mView.focusCamera(false);
        });

        inflate.setOnLongClickListener(arg0 -> {
            //perform auto focus and take picture
            focusAndTakePhoto();
            return true;
        });

        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addContentView(gestureOverlayView, layoutParamsControl);
    }

    Runnable mPlayTutorialReminder = new Runnable() {
        public void run() {
            ICSeeTutorial.playTutorialReminder(mContext);
        }
    };

    public void focusAndTakePhoto() {

        mView.mCamera.autoFocus((success, camera) -> {
            currentFilter = mView.curFilterSubset();
            if (currentFilter.equals("")) {
                mView.getPhoto(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG, 3);
            } else {
                mView.getPhoto(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG, 1);
            }
        });
    }

    Camera.ShutterCallback myShutterCallback = () -> {
    };

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            SoundPlayer.playSound(mContext, SoundPlayer.S7);
        }
    };

    Camera.PictureCallback myPictureCallback_JPG = (arg0, arg1) -> {
        Bitmap bitmapPicture
                = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
        mView.saveCurrentFilterSet(); // Store filter for later reference

        // Get current running filter
        if (!mView.curFilterSubset().equals("")) {
            Mat imgMAT = new Mat();
            Utils.bitmapToMat(bitmapPicture, imgMAT);
            // If the image is empty
            if (imgMAT.empty())
                return; // Ignore
            // Apply filters
            mView.applyCurrentFilters(imgMAT);
            Utils.matToBitmap(imgMAT, bitmapPicture);
        }

        startImageEdit(bitmapPicture);
    };

    private void startImageEdit(Bitmap bitmapPicture) {
        Intent intent = new Intent(mContext, ImageView.class);
        String dir = saveToInternalStorage(bitmapPicture);
        intent.putExtra("dir", dir);
        Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(250);
        startActivity(intent);
    }


    private String saveToInternalStorage(Bitmap bitmapImage) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, "profile.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 5, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert fos != null;
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mView = (RealtimeFilterView) findViewById(R.id.pbPreview);
        mView.setLongClickable(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ICSeeTutorial.stopSound();
    }

    @Override
    protected void onStop() {
        //Log.i(TAG, "onStop");
        super.onStop();
        if (mView != null)
            mView.disableView();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mView != null) {
            mView.enableView();
        }

        class mRunnable implements Runnable {
            @Override
            public void run() {
                while (mView == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                while (mView.getVisibility() != View.VISIBLE) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (mView.getVisibility() == View.VISIBLE) {
                    mView.setCvCameraViewListener(mView);

                    if (mView.lFilters.size() == 0) {
                        mView.appendFilter(new MatAdaptiveThresholding());      // black background, white letters
                        mView.appendFilter(new MatBinarizationFilter());        // white background, black letters
                        mView.appendFilter(new MatNegative());                  // negative
                        mView.appendFilter(new MatBlackYellowFilter());         // black background, yellow letters
                        mView.appendFilter(new MatBlueYellowFilter());          // blue background, yellow letters
                        mView.appendFilter(new MatBlueYellowInvertedFilter());  // yellow background, blue letters
                        mView.appendFilter(new MatWhiteRedFilter());            // white background, red letters
                    }
                    Log.i(TAG, "filters: " + mView.lFilters.toString());
                    // Restore last filter, if available
                    mView.restoreCurrentFilterSet();
                    // Re-enable view
                    mView.enableView();
                    mHandlerTutorial.postDelayed(mPlayTutorialReminder, 3000);
                }
            }
        }
        Thread tCheckForSurface = new Thread(new mRunnable());
        tCheckForSurface.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult called");
        super.onActivityResult(requestCode, resultCode, data);
        mView.enableView();
        mView.resumeCamera();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(ICSeeRealtimeActivity.this, "Back Button Pressed", Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        String sTheme;
        for (Prediction prediction : predictions) {
            if (prediction.score > 1.0) {
                if (prediction.name.contains("right")) {
                    sTheme = mView.nextFilterSubset();
                    // Process frame to show results
                    mView.process(1);

                    if (sTheme != null) {
                        logFilter(sTheme);
                        movementCounter++;
                        mView.saveCurrentFilterSet(); // Store filter for later reference
                        SoundPlayer.playSound(this.getApplicationContext(), SoundPlayer.S2);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!movementTutorial) {
                            if (ICSeeTutorial.getTutorialState(mContext) == 1) {
                                movementTutorial = true;
                            }
                            ICSeeTutorial.playChangedFilter(mContext);
                        } else if (movementCounter == 4) {
                            ICSeeTutorial.playAutoFocus(mContext);
                            movementCounter = 0;
                        }
                    } else {
                        mView.initFilterSubsets();
                        SoundPlayer.playSound(this.getApplicationContext(), SoundPlayer.S1);
                        ICSeeTutorial.playNoFiltersLeft(mContext);
                    }
                } else if (prediction.name.contains("left")) {

                    sTheme = mView.previousFilterSubset();
                    // Process frame to show results
                    mView.process(1);

                    if (sTheme != null) {
                        logFilter(sTheme);
                        movementCounter++;
                        mView.saveCurrentFilterSet(); // Store filter for later reference
                        SoundPlayer.playSound(this.getApplicationContext(), SoundPlayer.S3);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!movementTutorial) {
                            if (ICSeeTutorial.getTutorialState(mContext) == 1) {
                                movementTutorial = true;
                            }
                            ICSeeTutorial.playChangedFilter(mContext);
                        } else if (movementCounter == 4) {
                            ICSeeTutorial.playAutoFocus(mContext);
                            movementCounter = 0;
                        }
                    } else {
                        mView.initFilterSubsets();
                        SoundPlayer.playSound(this.getApplicationContext(), SoundPlayer.S1);
                        ICSeeTutorial.playNoFiltersRight(mContext);
                    }
                } else if (prediction.name.contains("omicron") || prediction.name.contains("omicron1") || prediction.name.contains("omicron2") || prediction.name.contains("omicron3") || prediction.name.contains("omicron4")) {
                    //if tutorial state is off
                    if (ICSeeTutorial.getTutorialState(mContext) == 0) {
                        ICSeeTutorial.tutorialOn();
                        ICSeeTutorial.playTutorialOn(mContext);
                        movementTutorial = false;
                        ICSeeTutorial.getTutorialState(mContext);
                    } else {
                        ICSeeTutorial.tutorialOff();
                        ICSeeTutorial.stopSound();
                        ICSeeTutorial.playTutorialOff(mContext);
                        ICSeeTutorial.getTutorialState(mContext);
                    }
                }
            }
        }
    }

    public static void logFilter(String sTheme) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", sTheme);
        analyticsController.sendEvent(mContext.getApplicationContext(), "filter_changed", sTheme, bundle);
    }
}

