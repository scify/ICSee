package gr.scify.icsee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;

import gr.scify.icsee.camera.ModifiedLoaderCallback;

import static gr.scify.icsee.ICSeeStartActivity.*;

/**
 * Created by scifi on 6/8/2014.
 */
public class AsyncProgressCheck extends AsyncTask<Void, Void, Void> {
    Context mContext;
    ProgressDialog mDialog;
    ModifiedLoaderCallback mOpenCVCallBack;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();
    AsyncProgressCheck (ProgressDialog pd, ModifiedLoaderCallback aa,Context cc) {
        mDialog = pd;
        mOpenCVCallBack = aa;
        mContext=cc;
    }


    @Override
    protected Void doInBackground(Void... params) {
        while (mOpenCVCallBack.processStatus == Integer.MIN_VALUE) {
            try {
                Thread.sleep(500);
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
                    Intent strt = new Intent(mContext,ICSeeRealtimeActivity.class);
                    strt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(strt);
                    break;
            }
                default: {
                    Log.i(TAG, "OpenCV didn't load successfully");
                    }
         break;
        }
        return;
    }
}