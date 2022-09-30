package gr.scify.icsee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.opencv.android.LoaderCallbackInterface;

import gr.scify.icsee.camera.ModifiedLoaderCallback;

public class AsyncProgressCheck extends AsyncTask<Void, Void, Void> {
    ICSeeStartActivity mContext;
    ProgressDialog mDialog;
    ModifiedLoaderCallback mOpenCVCallBack;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();

    public AsyncProgressCheck (ProgressDialog pd, ModifiedLoaderCallback aa,ICSeeStartActivity cc) {
        mDialog = pd;
        mOpenCVCallBack = aa;
        mContext=cc;
    }

    @Override
    protected Void doInBackground(Void... params) {
    	// Check for back camera
    	// If not found
    	if (gr.scify.icsee.camera.Utils.findFrontFacingCamera() == -1) {
    		mContext.showErrorMessage("No back camera found.", true);
    		return null;
    	}
    	// While there has been no return from OpenCV
        while (mOpenCVCallBack.processStatus < 0) {
            try {
            	// Wait for a while
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
                    // Prepare new intent
                    Intent strt = new Intent(mContext,ICSeeRealtimeActivity.class);
                    strt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(strt);
                    mContext.finish();
                    break;
                }
                case LoaderCallbackInterface.INSTALL_CANCELED:  {
                    Log.i(TAG, "OpenCV installation cancelled by user.");
                    mContext.showErrorMessage("OpenCV was not installed. Cannot continue.", 
                    		true);
                    return;
                }
                default: {
                    Log.i(TAG, "OpenCV didn't load successfully");
                    mContext.showErrorMessage("OpenCV didn't load successfully. Cannot continue.", 
                    		true);
                }
        }

        return;
    }
}