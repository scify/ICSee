package gr.scify.icsee.camera;

import android.content.Context;

import org.opencv.android.BaseLoaderCallback;

/**
 * Created by scifi on 1/8/2014.
 */
public class ModifiedLoaderCallback extends BaseLoaderCallback {
    public int processStatus=Integer.MIN_VALUE; // Not yet initialized


    public ModifiedLoaderCallback(Context AppContext) {
        super(AppContext);

    }

    @Override
        public void onManagerConnected(int status){
            processStatus = status;

            super.onManagerConnected(status);
        }
}
