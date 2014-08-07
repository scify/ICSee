package gr.scify.icsee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

import gr.scify.icsee.camera.ModifiedLoaderCallback;
import gr.scify.icsee.camera.RealtimeFilterView;


public class ICSeeStartActivity extends Activity {

    protected Context mContext;
    public ModifiedLoaderCallback mOpenCVCallBack;
    public ProgressDialog mDialog;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        mOpenCVCallBack = new ModifiedLoaderCallback(this, (RealtimeFilterView) findViewById(R.id.pbPreview));
        Log.i(TAG, "Trying to load OpenCV library");

    }

    protected void onPause() {
        super.onPause();

    }

    protected void onResume() {
        super.onResume();

//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mOpenCVCallBack);
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mOpenCVCallBack)) {
            Log.e(TAG, "Cannot start connecting to OpenCV Manager");
        }
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...OpenCV is Loading");
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.show();

        new AsyncProgressCheck(mDialog, mOpenCVCallBack,mContext).execute();


 }


}

