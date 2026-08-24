package gr.scify.icsee.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import gr.scify.icsee.AsyncProgressCheck;
import gr.scify.icsee.ICSeeStartActivity;
import gr.scify.icsee.ICSeeTutorial;

/**
 * Created by scifi on 1/8/2014.
 */
public class ModifiedLoaderCallback {
    // Matches the status value the old org.opencv.android.LoaderCallbackInterface.SUCCESS used,
    // kept since OpenCV 4.x no longer ships that interface.
    public static final int SUCCESS = 0;

    public int processStatus = Integer.MIN_VALUE; // Not yet initialized
    protected String TAG = ModifiedLoaderCallback.class.getCanonicalName();
    ProgressBar mProgressBar;
    Context mContext;
    public boolean hasManagerConnected = false;
    public ProgressDialog mDialog;
    public ModifiedLoaderCallback mThis;
    public ICSeeStartActivity startActivity;


    public ModifiedLoaderCallback(Context AppContext, ProgressBar progressBar, ProgressDialog dialog, ICSeeStartActivity start) {
        mProgressBar = progressBar;
        mContext = AppContext;
        mDialog = dialog;
        mThis = this;
        startActivity = start;
    }

    public void onManagerConnected(int status) {
        processStatus = status;

        if (processStatus == SUCCESS) {
            mProgressBar.setVisibility(View.GONE);
            hasManagerConnected = true;
            if (ICSeeTutorial.getTutorialState(mContext) == 0) {
                new AsyncProgressCheck(mDialog, this, startActivity).execute();
            } else {
                ICSeeTutorial.playWelcome(mContext);
            }
        }
    }
}
