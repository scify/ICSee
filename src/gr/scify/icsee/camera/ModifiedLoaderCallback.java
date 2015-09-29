package gr.scify.icsee.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import gr.scify.icsee.AsyncProgressCheck;
import gr.scify.icsee.ICSeeStartActivity;
import gr.scify.icsee.R;
import gr.scify.icsee.sounds.SoundPlayer;

/**
 * Created by scifi on 1/8/2014.
 */
public class ModifiedLoaderCallback extends BaseLoaderCallback {
    public int processStatus=Integer.MIN_VALUE; // Not yet initialized
    ProgressBar mPrograssBar;
    Context mContext;
    public boolean hasManagerConnected = false;
    static MediaPlayer mplayer;
    public ProgressDialog mDialog;
    public ModifiedLoaderCallback mThis;


    public ModifiedLoaderCallback(Context AppContext, ProgressBar progressBar, MediaPlayer mp, ProgressDialog dialog) {
        super(AppContext);
        mPrograssBar = progressBar;
        mContext = AppContext;
        mplayer = mp;
        mDialog = dialog;
        mThis = this;
    }

    @Override
        public void onManagerConnected(int status){
            processStatus = status;

            super.onManagerConnected(status);
            if(processStatus == LoaderCallbackInterface.SUCCESS) {
                mPrograssBar.setVisibility(View.INVISIBLE);
                hasManagerConnected = true;
                playTutorial(mContext);
                //SoundPlayer.playSound(mContext, SoundPlayer.Stutorial);
            }
        }

    private void playTutorial(Context context) {
        if(mplayer != null) {
            if(mplayer.isPlaying()){
                mplayer.stop();
            } else {
                mplayer = MediaPlayer.create(context, R.raw.tutorial);
                Log.i("ModifiedLoaderCallback", "about to play");
                mplayer.start();
            }
        } else {
            mplayer = MediaPlayer.create(context,R.raw.tutorial);
            mplayer.start();
        }
    }

    public void stopTutorial(){
        mplayer.stop();
    }
}
