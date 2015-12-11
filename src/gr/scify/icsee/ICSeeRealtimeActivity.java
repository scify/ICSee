package gr.scify.icsee;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import gr.scify.icsee.camera.ImageView;
import gr.scify.icsee.camera.RealtimeFilterView;
import gr.scify.icsee.filters.opencv.matfilters.MatAdaptiveThresholding;
import gr.scify.icsee.filters.opencv.matfilters.MatBinarizationFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatBlackYellowFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatBlueYellowFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatBlueYellowInvertedFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatNegative;
import gr.scify.icsee.filters.opencv.matfilters.MatWhiteRedFilter;
import gr.scify.icsee.sounds.SoundPlayer;

public class ICSeeRealtimeActivity extends Activity implements OnGesturePerformedListener {
    public boolean RTStarted = false;
    public ProgressDialog mDialog;
    private GestureLibrary gestureLib;
    protected int MAX_ZOOM = 5;
    public RealtimeFilterView mView = null;
    private static Context mContext;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext = this;
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
        inflate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Play start focus sound
                SoundPlayer.playSound(arg0.getContext(), SoundPlayer.S1);

                mView.focusCamera();
            }
        });

        inflate.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                // Resume or pause the camera
                /*if (mView.camerastate() == false) {
                    mView.pauseCamera();
                } else {
                    mView.resumeCamera();
                }*/

                focusCamera();

                return true;
            }

        });

        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addContentView(gestureOverlayView, layoutParamsControl);
    }

    public void focusCamera() {

        mView.mCamera.autoFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                String currentFilter = mView.curFilterSubset().toString();
                if(currentFilter.equals("")) {
                    mView.getPhoto(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG, 3);
                }else {
                    mView.getPhoto(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG, 1);
                }

            }
        });
    }

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback(){

        @Override
        public void onShutter() {

        }};

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            SoundPlayer.playSound(mContext, SoundPlayer.S7);
        }};

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            Bitmap bitmapPicture
                    = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
            String currentFilter = mView.curFilterSubset().toString();
            if(!currentFilter.equals("")) {

                Mat imgMAT = new Mat();
                Utils.bitmapToMat(bitmapPicture, imgMAT);
                mView.applyCurrentFilters(imgMAT);
                Utils.matToBitmap(imgMAT, bitmapPicture);
            } else {
                Log.i(TAG, "no filter");
            }
            startImageEdit(bitmapPicture);
        }};

    private void startImageEdit(Bitmap bitmapPicture) {
        Intent intent = new Intent(mContext, ImageView.class);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Log.i(TAG, "height: " + bitmapPicture.getHeight());
        Log.i(TAG, "width: " + bitmapPicture.getWidth());
        String dir = saveToInternalSorage(bitmapPicture);
        intent.putExtra("dir", dir);
        Log.i(TAG, "about to start the activity");
        startActivity(intent);
    }


    private String saveToInternalSorage(Bitmap bitmapImage){

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        if(directory.exists()) {
            Log.i(TAG, "Woohoo");
        }
        File mypath=new File(directory,"profile.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 5, fos);
            Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(500);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
        mView.appendFilter(new MatAdaptiveThresholding());      // black background, white letters
        mView.appendFilter(new MatBinarizationFilter());        // white background, black letters
        mView.appendFilter(new MatNegative());                  // negative
        mView.appendFilter(new MatBlackYellowFilter());         // black background, yellow letters
        mView.appendFilter(new MatBlueYellowFilter());          // blue background, yellow letters
        mView.appendFilter(new MatBlueYellowInvertedFilter());  // yellow background, blue letters
        mView.appendFilter(new MatWhiteRedFilter());            // white background, red letters

    }

    @Override
    protected void onPause() {
        //Log.i(TAG, "onPause");
        super.onPause();
        /*if (mView != null)
            mView.disableView();*/
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
        //Log.i(TAG, "onResume");
        super.onResume();
        if (mView != null){
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
                    mView.enableView();
                }
            }
        }

        Thread tCheckForSurface = new Thread(new mRunnable());
        tCheckForSurface.start();

    }

    public void showFilters() {
        String sTheme = mView.curFilterSubset();
        Toast.makeText(ICSeeRealtimeActivity.this, "Next Theme: " + sTheme, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mView.enableView();
        mView.resumeCamera();
        finish();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Toast.makeText(ICSeeRealtimeActivity.this, "Back Button Pressed",Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    protected List<List<Object>> getCombinationsBy(Object oObj, int iBySize) {
        List<List<Object>> uRes = new ArrayList<List<Object>>();

        List<Object> lList;
        // If unary, wrap in list.
        if (!(oObj instanceof List)) {
            lList = new ArrayList<Object>();
            lList.add(oObj);
        } else
            lList = (List<Object>) oObj;

        int[] indices;
        CombinationGenerator cgGen = new CombinationGenerator(lList.size(),
                iBySize);
        while (cgGen.hasMore()) {
            List<Object> cComb = new ArrayList<Object>();
            indices = cgGen.getNext();
            for (int i = 0; i < indices.length; i++) {
                cComb.add(lList.get(indices[i]));
            }
            uRes.add(cComb);
        }
        return uRes;
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        String sTheme;
        TextView sliderText = (TextView) findViewById(R.id.verticalSeekbarText);
        for (Prediction prediction : predictions) {
            if (prediction.score > 1.0) {
//                    Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
//                      .show();
                if (prediction.name.contains("right")) {
                    sTheme = mView.nextFilterSubset();
                    // Process frame to show results
                    mView.process(1);

                    if (sTheme != null) {
                        sliderText.setText("Next Theme: " + sTheme);
                        /*Toast.makeText(ICSeeRealtimeActivity.this, "Next Theme: " + sTheme,
                        	Toast.LENGTH_SHORT).show();*/
                        SoundPlayer.playSound(this.getApplicationContext(), SoundPlayer.S2);
                    } else {
                        mView.initFilterSubsets();
                        sliderText.setText("No theme applicable");
                        /*Toast.makeText(ICSeeRealtimeActivity.this, "No theme applicable",
                        		Toast.LENGTH_SHORT).show();*/
                        SoundPlayer.playSound(this.getApplicationContext(), SoundPlayer.S6);
                    }
                } else if (prediction.name.contains("left")) {
                    sTheme = mView.previousFilterSubset();
                    // Process frame to show results
                    mView.process(1);

                    if (sTheme != null) {
                        sliderText.setText("Previous Theme: " + sTheme);
                        /*Toast.makeText(ICSeeRealtimeActivity.this, "Previous Theme: " + sTheme,
                        	Toast.LENGTH_SHORT).show();*/
                        SoundPlayer.playSound(this.getApplicationContext(), SoundPlayer.S3);
                    } else {
                        mView.initFilterSubsets();
                        sliderText.setText("No theme applicable");
                        	/*Toast.makeText(ICSeeRealtimeActivity.this, "No theme applicable",
                        			Toast.LENGTH_SHORT).show();*/
                        SoundPlayer.playSound(this.getApplicationContext(), SoundPlayer.S6);
                    }
                }
            }
        }
    }
}

