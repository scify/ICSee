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

public class OpenCVContourDetection extends OpenCVNegative {
	// Init resources
    protected Mat mGraySubmat = new Mat();

	@Override
	public IBitmapFilter applyfilter() {
	    
		// Convert bitmap to mat
		Utils.bitmapToMat(lastBitmap, mIntermediateMat);
		Imgproc.cvtColor(mIntermediateMat, mGraySubmat, Imgproc.COLOR_RGB2GRAY, 1);
		// Init contour output
		ArrayList<MatOfPoint> acContours = new ArrayList<MatOfPoint>();
		Mat mHier = new Mat();
		Mat mContours = Mat.zeros(mIntermediateMat.size(), CvType.CV_8UC1);
		Imgproc.findContours(mGraySubmat, acContours, mHier,
				Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Scalar sColor = new Scalar(255, 255, 255);
		// For each contour
		for (int iCnt = 0; iCnt < acContours.size(); iCnt++) {
			// Draw contour
			Imgproc.drawContours(mContours, acContours, iCnt, sColor);
		}
		
		// Apply on output
		Imgproc.cvtColor(mContours, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
	    try {
	        Utils.matToBitmap(mRGBA, this.lastBitmap);
	    } catch(Exception e) {
	        Log.e(this.getClass().getCanonicalName(), "Could not convert image to bitmap: " + e.getMessage());
	    }
		
	    // Release resources
	    acContours.clear();
	    for (MatOfPoint mCur : acContours)
	    	mCur.release();
	    mHier.release();
	    
	    
		return this;
	}
	@Override
	protected void finalize() throws Throwable {
	    mGraySubmat.release();
	    mGraySubmat = null;
	    
		super.finalize();
	}

	@Override
	public String toString() {
		return "Detect contours";
	}
}
