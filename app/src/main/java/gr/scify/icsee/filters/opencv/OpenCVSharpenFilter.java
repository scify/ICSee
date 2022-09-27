package gr.scify.icsee.filters.opencv;

import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IBitmapFilter;

public class OpenCVSharpenFilter extends OpenCVNegative {
	@Override
	public IBitmapFilter applyfilter() {
		synchronized (this.lastBitmap) {
			// Convert bitmap to mat
			Utils.bitmapToMat(lastBitmap, mRGBA);
			// Apply blur and remove with weight
		    double[] dFilterParam = new double []{0,0};
			Imgproc.GaussianBlur(mRGBA, mIntermediateMat, new Size(dFilterParam), 3);
			Core.addWeighted(mRGBA, 1.5, mIntermediateMat, -0.5, 0, mRGBA);
			
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
		return "Sharpen";
	}}
