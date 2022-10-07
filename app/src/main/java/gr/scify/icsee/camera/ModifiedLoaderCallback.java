package gr.scify.icsee.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import gr.scify.icsee.AsyncProgressCheck;
import gr.scify.icsee.ICSeeStartActivity;
import gr.scify.icsee.ICSeeTutorial;

/**
 * Created by scifi on 1/8/2014.
 */
public class ModifiedLoaderCallback extends BaseLoaderCallback {
    public int processStatus = Integer.MIN_VALUE; // Not yet initialized
    protected String TAG = ModifiedLoaderCallback.class.getCanonicalName();
    ProgressBar mProgressBar;
    Context mContext;
    public boolean hasManagerConnected = false;
    public ProgressDialog mDialog;
    public ModifiedLoaderCallback mThis;
    public ICSeeStartActivity startActivity;


    public ModifiedLoaderCallback(Context AppContext, ProgressBar progressBar, ProgressDialog dialog, ICSeeStartActivity start) {
        super(AppContext);
        mProgressBar = progressBar;
        mContext = AppContext;
        mDialog = dialog;
        mThis = this;
        startActivity = start;
    }

    public void setProgressBar(ProgressBar mProgressBar) {
        this.mProgressBar = mProgressBar;
    }

    @Override
    public void onManagerConnected(int status) {
        processStatus = status;

        super.onManagerConnected(status);
        if (processStatus == LoaderCallbackInterface.SUCCESS) {
            mProgressBar.setVisibility(View.GONE);
            hasManagerConnected = true;
            if (ICSeeTutorial.getTutorialState(mContext) == 0) {
                new AsyncProgressCheck(mDialog, ICSeeStartActivity.mOpenCVCallBack, startActivity).execute();
            } else {
                ICSeeTutorial.playWelcome(mContext);
            }
        }
    }

    public void setContext(Context context) {
        this.mContext = context;
    }
}
