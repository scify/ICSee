package gr.scify.icsee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.core.Mat;
import org.opencv.android.OpenCVLoader;

import gr.scify.icsee.camera.ModifiedLoaderCallback;
import gr.scify.icsee.camera.RealtimeFilterView;


public class ICSeeStartActivity extends Activity {

    protected Context mContext;
    public ModifiedLoaderCallback mOpenCVCallBack;
    public ProgressDialog mDialog;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();
    boolean bFirstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        mContext = this;
        
        mOpenCVCallBack = new ModifiedLoaderCallback(this);
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Trying to load OpenCV library");
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mOpenCVCallBack);


        new AsyncProgressCheck(mDialog, mOpenCVCallBack,this).execute();
    }

    public void showErrorMessage(String sMsg, boolean bShouldClose) {
        // Hide progress bar
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(pb.INVISIBLE);

        // Set errorMessage text
        TextView tvMsg = (TextView)findViewById(R.id.errorMessage);
        tvMsg.setText(sMsg);

        // Show errorMessage text
        tvMsg.setVisibility(tvMsg.VISIBLE);

        // Show button
        Button bExitBtn = (Button)findViewById(R.id.exitButton);
        bExitBtn.setVisibility(bExitBtn.VISIBLE);

        // If bShouldClose
        if (bShouldClose) {
            bExitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // exit activity
                    finish();
                }
            });
        }
    }
}

