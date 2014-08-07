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
    protected RealtimeFilterView mView;


    public ModifiedLoaderCallback(Context AppContext, RealtimeFilterView mView) {
        super(AppContext);
        this.mView = mView;

    }

    @Override
        public void onManagerConnected(int status){
            processStatus = status;

            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    // Allow long clicks

                    mView.setLongClickable(true);
                    mView.appendFilter(new MatAdaptiveThresholding());
                    mView.appendFilter(new MatBinarizationFilter());
                    mView.appendFilter(new MatNegative());

//					mView.appendFilter(new MatHistogramEqualization());
//					mView.appendFilter(new MatSmoothFilterMedian());
//					mView.appendFilter(new MatBlurFilter());
//					mView.appendFilter(new MatEdgeDetectionCannyFilter());
//				    mView.appendFilter(new MatBlueFilter());
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
}
