package gr.scify.icsee.filters.opencv;

import gr.scify.icsee.filters.IBitmapFilter;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class OpenCVIntensityBlur extends OpenCVNegative {
	@Override
	public IBitmapFilter applyfilter() {
		synchronized (this.lastBitmap) {
			// Convert bitmap to mat
			Utils.bitmapToMat(lastBitmap, mRGBA);
			// Apply blur and remove with weight
		    double[] dFilterParam = new double []{3,3};
			Imgproc.GaussianBlur(mRGBA, mIntermediateMat, new Size(dFilterParam), 5.0, 5.0);
			Core.addWeighted(mRGBA, 1.5, mIntermediateMat, 0.5, 0, mRGBA);
			
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
		return "IntensityBlur";
	}
}
