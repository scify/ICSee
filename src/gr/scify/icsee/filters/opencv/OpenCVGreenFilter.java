package gr.scify.icsee.filters.opencv;

import gr.scify.icsee.filters.IBitmapFilter;
import gr.scify.icsee.filters.cpu.GrayScaleFilter;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class OpenCVGreenFilter extends GrayScaleFilter {
    protected Mat mIntermediateMat = new Mat();
    protected Mat mRGBA = new Mat();
    protected Mat mB = new Mat();
    protected Mat mG = new Mat();
    protected Mat mR = new Mat();
	
    @Override
    protected void finalize() throws Throwable {
    	super.finalize();
    	    	
    	// Release resources
        mB.release();
        mB = null;
        mG.release();
        mG = null;
        mR.release();
        mR = null;
        mRGBA.release();
        mRGBA = null;
        mIntermediateMat.release();
        mIntermediateMat = null;
    }
    
	@Override
	public IBitmapFilter applyfilter() {
		synchronized (this.lastBitmap) {
			// Convert bitmap to mat
			Utils.bitmapToMat(lastBitmap, mRGBA);
		    // Convert to grayscale
		    Imgproc.cvtColor(mRGBA, mG, Imgproc.COLOR_BGRA2GRAY, 1);
			
			// Init split channels list
			List<Mat> lSrc = new ArrayList<Mat>();
			// Zero-out other channels
			mB = Mat.zeros(mG.rows(), mG.cols(), mG.type());
			mR = Mat.zeros(mG.rows(), mG.cols(), mG.type());
		    lSrc.add(mR); 
		    lSrc.add(mG); 
		    lSrc.add(mB); 
			
			Core.merge(lSrc, mRGBA);
			
		    try {
		        Utils.matToBitmap(mRGBA, this.lastBitmap);
		    } catch(Exception e) {
		        Log.e(this.getClass().getCanonicalName(), "Could not convert image to bitmap: " + e.getMessage());
		    }
		}
		
		return this;
	}

	@Override
	public String toString() {
		return "Green";
	}
}
