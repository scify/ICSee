package gr.scify.icsee.filters.opencv;

import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IBitmapFilter;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;
import static org.opencv.imgproc.Imgproc.threshold;

public class OpenCVTextReaderFilter extends OpenCVNegative {
	@Override
	public IBitmapFilter applyfilter() {
		synchronized (this.lastBitmap) {
			// Convert bitmap to mat
			Utils.bitmapToMat(lastBitmap, mRGBA);		    
		    // Sharpen
//		    Imgproc.GaussianBlur(mRGBA, mIntermediateMat, new Size(3,3), 3.0);
//		    addWeighted(mIntermediateMat, 1.5, mRGBA, -0.5, 0, mRGBA);
		    
		    // Convert to grayscale
		    Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);
		    
			// Apply histogram equalization
			Imgproc.equalizeHist(mIntermediateMat, mIntermediateMat);

			// Apply invert threshold
		    threshold(mIntermediateMat, mIntermediateMat, 20, 128, THRESH_BINARY_INV);
		    
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
		return "TextReader (v0.5)";
	}
}
