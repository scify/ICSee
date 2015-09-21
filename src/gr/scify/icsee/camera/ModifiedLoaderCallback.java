package gr.scify.icsee.camera;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import gr.scify.icsee.sounds.SoundPlayer;

/**
 * Created by scifi on 1/8/2014.
 */
public class ModifiedLoaderCallback extends BaseLoaderCallback {
    public int processStatus=Integer.MIN_VALUE; // Not yet initialized
    ProgressBar mPrograssBar;
    Context mContext;


    public ModifiedLoaderCallback(Context AppContext, ProgressBar progressBar) {
        super(AppContext);
        mPrograssBar = progressBar;
        mContext = AppContext;
    }

    @Override
        public void onManagerConnected(int status){
            processStatus = status;

            super.onManagerConnected(status);
            if(processStatus == LoaderCallbackInterface.SUCCESS) {
                mPrograssBar.setVisibility(View.INVISIBLE);
                SoundPlayer.playSound(mContext, SoundPlayer.Stutorial);
            }
        }
}
