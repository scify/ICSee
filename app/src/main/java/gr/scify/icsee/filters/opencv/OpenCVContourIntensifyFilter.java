package gr.scify.icsee.filters.opencv;

import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import gr.scify.icsee.filters.IBitmapFilter;

public class OpenCVContourIntensifyFilter extends OpenCVEdgeDetectionCanny {
    public IBitmapFilter applyfilter() {
        synchronized (this.lastBitmap) {

            // Convert bitmap to mat
            Utils.bitmapToMat(lastBitmap, mIntermediateMat);
            Imgproc.cvtColor(mIntermediateMat, mGraySubmat, Imgproc.COLOR_RGB2GRAY, 1);

            // Init contour output
            ArrayList<MatOfPoint> acContours = new ArrayList<MatOfPoint>();
            Mat mContours = Mat.zeros(mGraySubmat.size(), CvType.CV_8UC1);
            Mat mHier = new Mat();
            Imgproc.findContours(mGraySubmat, acContours, mHier,
                    Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            Scalar sColor = new Scalar(255, 255, 255);
            // For each contour
            for (int iCnt = 0; iCnt < acContours.size(); iCnt++) {
                // Draw contour with thickness 3
                Imgproc.drawContours(mContours, acContours, iCnt, sColor, 3);
            }
            // Apply on output
            Imgproc.cvtColor(mContours, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
        }

        try {
            Utils.matToBitmap(mRGBA, this.lastBitmap);
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Could not convert image to bitmap: " + e.getMessage());
        }

        return this;
    }

    @Override
    public String toString() {
        return "Intensify contours";
    }
}
