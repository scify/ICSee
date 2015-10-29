package gr.scify.icsee.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import java.util.Locale;

import gr.scify.icsee.AsyncProgressCheck;
import gr.scify.icsee.ICSeeStartActivity;
import gr.scify.icsee.R;

/**
 * Created by scifi on 1/8/2014.
 */
public class ModifiedLoaderCallback extends BaseLoaderCallback {
    public int processStatus=Integer.MIN_VALUE; // Not yet initialized
    ProgressBar mPrograssBar;
    Context mContext;
    public boolean hasManagerConnected = false;
    public MediaPlayer mplayer;
    public ProgressDialog mDialog;
    public ModifiedLoaderCallback mThis;
    public ICSeeStartActivity startActivity;


    public ModifiedLoaderCallback(Context AppContext, ProgressBar progressBar, MediaPlayer mp, ProgressDialog dialog, ICSeeStartActivity start) {
        super(AppContext);
        mPrograssBar = progressBar;
        mContext = AppContext;
        mplayer = mp;
        mDialog = dialog;
        mThis = this;
        startActivity = start;
    }

    @Override
        public void onManagerConnected(int status){
            processStatus = status;

            super.onManagerConnected(status);
            if(processStatus == LoaderCallbackInterface.SUCCESS) {
                mPrograssBar.setVisibility(View.INVISIBLE);
                hasManagerConnected = true;
                playTutorial(mContext);
            }
        }

    private void playTutorial(Context context) {
        String lang = Locale.getDefault().getLanguage();
        TelephonyManager tm = (TelephonyManager)startActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        Log.i("lang", "lang: " + lang);
        Log.i("lang", "country: " + countryCode);
        if(mplayer != null) {
            if(mplayer.isPlaying()){
                mplayer.stop();
            } else {

                if(lang.equals("el") || countryCode.equals("gr")) {
                    mplayer = MediaPlayer.create(context, R.raw.tutorial);
                } else {
                    mplayer = MediaPlayer.create(context, R.raw.tutorial_en);
                }
                mplayer.start();
            }
        } else {
            if(lang.equals("el") || countryCode.equals("gr")) {
                mplayer = MediaPlayer.create(context, R.raw.tutorial);
            } else {
                mplayer = MediaPlayer.create(context, R.raw.tutorial_en);
            }
            mplayer.start();
        }
        mplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPrograssBar.setVisibility(View.VISIBLE);
                new AsyncProgressCheck(mDialog, ICSeeStartActivity.mOpenCVCallBack, startActivity).execute();
            }
        });

    }

    public void stopTutorial(){
        mplayer.stop();
    }
}
