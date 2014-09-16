package gr.scify.icsee.camera;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import gr.scify.icsee.ICSeeRealtimeActivity;
import gr.scify.icsee.filters.opencv.matfilters.MatAdaptiveThresholding;
import gr.scify.icsee.filters.opencv.matfilters.MatBinarizationFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatNegative;

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

//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                {
//
//                } break;
//                default:
//                {
//
//                } break;
//            }
            super.onManagerConnected(status);
        }
}
