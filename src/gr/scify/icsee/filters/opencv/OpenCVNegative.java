package gr.scify.icsee.filters.opencv;

import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IBitmapFilter;
import gr.scify.icsee.filters.cpu.GrayScaleFilter;

public class OpenCVNegative extends GrayScaleFilter {
    protected Mat mIntermediateMat = new Mat();
    protected Mat mRGBA = new Mat();
    
    
    @Override
    protected void finalize() throws Throwable {
    	super.finalize();
    	
    	// Release resources
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
		    Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);
			
			// Subtract from 255 matrix the mRGBA
			Core.bitwise_not(mIntermediateMat, mIntermediateMat);
			
			// Convert back to color
		    Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
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
		return "Negative";
	}
}

