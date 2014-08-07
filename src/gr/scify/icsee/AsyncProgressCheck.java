package gr.scify.icsee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.opencv.android.LoaderCallbackInterface;

import gr.scify.icsee.camera.ModifiedLoaderCallback;

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
        switch (mOpenCVCallBack.processStatus){
            case LoaderCallbackInterface.SUCCESS:
            {
                Log.i(TAG, "OpenCV loaded successfully");


                return null;
            }

            default:
            {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } break;

        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Intent strt = new Intent(mContext,ICSeeRealtimeActivity.class);

        mContext.startActivity(strt);

    }
}