package gr.scify.icsee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.opencv.android.LoaderCallbackInterface;

import gr.scify.icsee.camera.ModifiedLoaderCallback;

public class AsyncProgressCheck extends AsyncTask<Void, Void, Void> {
    protected Activity mContext;
    ProgressDialog mDialog;
    ModifiedLoaderCallback mOpenCVCallBack;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();

    public AsyncProgressCheck(ProgressDialog pd, ModifiedLoaderCallback aa, Activity cc) {
        mDialog = pd;
        mOpenCVCallBack = aa;
        mContext = cc;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Check for back camera
        // If not found
        if (gr.scify.icsee.camera.Utils.findFrontFacingCamera() == -1) {
            return null;
        }
        // While there has been no return from OpenCV
        while (mOpenCVCallBack.processStatus < 0) {
            try {
                // Wait for a while
                this.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        switch (mOpenCVCallBack.processStatus) {
            case LoaderCallbackInterface.SUCCESS: {
                Log.i(TAG, "OpenCV loaded successfully");
                // Prepare new intent
                Intent intent = new Intent(mContext, ICSeeRealtimeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.finish();
                mContext.startActivity(intent);
                break;
            }
            case LoaderCallbackInterface.INSTALL_CANCELED: {
                Log.i(TAG, "OpenCV installation cancelled by user.");
                return;
            }
            default: {
                Log.i(TAG, "OpenCV didn't load successfully");
            }
        }
    }
}