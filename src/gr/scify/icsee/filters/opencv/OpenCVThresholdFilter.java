package gr.scify.icsee.filters.opencv;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;
import gr.scify.icsee.filters.IBitmapFilter;

import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class OpenCVThresholdFilter extends OpenCVNegative {
	@Override
	public IBitmapFilter applyfilter() {
		synchronized (this.lastBitmap) {
			// Convert bitmap to mat
			Utils.bitmapToMat(lastBitmap, mRGBA);
		    // Convert to grayscale
		    Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);
			
			// Apply threshold
		    threshold(mIntermediateMat, mIntermediateMat, 150, 255, THRESH_BINARY);
		    
		    // Convert to color
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
		return "Threshold";
	}

}
