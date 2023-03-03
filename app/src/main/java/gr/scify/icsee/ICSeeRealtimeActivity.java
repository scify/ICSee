package gr.scify.icsee;

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

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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

public class ICSeeRealtimeActivity extends LocalizedActivity implements OnGesturePerformedListener {
    private GestureLibrary gestureLib;
    public RealtimeFilterView mView = null;
    protected Context mContext;
    protected String TAG = ICSeeRealtimeActivity.class.getName();
    protected Handler mHandlerTutorial;
    private static boolean movementTutorial = false;
    private static int movementCounter = 0;
    protected static AnalyticsController analyticsController;
    protected Camera.ShutterCallback myShutterCallback;
    protected Camera.PictureCallback myPictureCallback_JPG;
    Runnable mPlayTutorialReminder;
    protected Intent frozenImageIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandlerTutorial = new Handler();
        mContext = this;
        analyticsController = AnalyticsController.getInstance();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View view = View.inflate(mContext, R.layout.custom, null);
        gestureOverlayView.addView(view);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        setPictureCallbacks();
        if (!gestureLib.load()) {
            finish();
        }
        // Init Handler
        setContentView(R.layout.activity_main);
        view.setOnClickListener(arg0 -> {
            // Play start focus sound
            SoundPlayer.playSound(arg0.getContext(), SoundPlayer.S9);
            mView.focusCamera(false);
        });

        view.setOnLongClickListener(arg0 -> {
            //perform auto focus and take picture
            try {
                handleLongClickEvent();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });

        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addContentView(gestureOverlayView, layoutParamsControl);
        mPlayTutorialReminder = () -> ICSeeTutorial.playTutorialReminder(mContext);
        mView = findViewById(R.id.pbPreview);
        mView.setLongClickable(true);
    }

    public void handleLongClickEvent() {
        focusAndTakePhoto();
    }

    public void focusAndTakePhoto() {
        mView.mCamera.autoFocus((success, camera) ->
                mView.getPhotoAndFreeze(myShutterCallback, null, myPictureCallback_JPG));
    }

    public void setPictureCallbacks() {
        myShutterCallback = () -> {
            Log.d(TAG, "Callback Shutter");
            SoundPlayer.playSound(mContext, SoundPlayer.S7);
        };

        myPictureCallback_JPG = (arg0, arg1) -> {
            Log.d(TAG, "Callback JPG");

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

            startImageActivity(bitmapPicture);
        };
    }


    private void startImageActivity(Bitmap bitmapPicture) {
        frozenImageIntent = new Intent(mContext, ImageViewerActivity.class);
        String dir = saveToInternalStorage(bitmapPicture);
        frozenImageIntent.putExtra("dir", dir);
        Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(250);
        startActivity(frozenImageIntent);
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
        Log.d(TAG, "Realtime activity resumed");
        super.onResume();
        class mRunnable implements Runnable {
            @Override
            public void run() {
                while (mView == null || mView.getVisibility() != View.VISIBLE) {
                    try {
                        this.wait(300);
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
                    mView.resumeCamera();
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
    public void onBackPressed() {
        super.onBackPressed();
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

    public void logFilter(String sTheme) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", sTheme);
        analyticsController.sendEvent(mContext.getApplicationContext(), "filter_changed", sTheme, bundle);
    }
}

