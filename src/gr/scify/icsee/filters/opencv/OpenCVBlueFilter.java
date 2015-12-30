package gr.scify.icsee.filters.opencv;

import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import gr.scify.icsee.filters.IBitmapFilter;

public class OpenCVBlueFilter extends OpenCVGreenFilter {
	
	@Override
	public IBitmapFilter applyfilter() {
		synchronized (this.lastBitmap) {
			// Convert bitmap to mat
			Utils.bitmapToMat(lastBitmap, mRGBA);
		    // Convert to RGB again
		    Imgproc.cvtColor(mRGBA, mB, Imgproc.COLOR_BGRA2GRAY, 1);
			
			// Init split channels list
			List<Mat> lSrc = new ArrayList<Mat>();
			// Zero-out other channels
			mG = Mat.zeros(mB.rows(), mB.cols(), mB.type());
			mR = Mat.zeros(mB.rows(), mB.cols(), mB.type());
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
		return "Blue";
	}
}
