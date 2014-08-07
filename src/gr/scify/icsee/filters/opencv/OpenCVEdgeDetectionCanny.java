package gr.scify.icsee.filters.opencv;

import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IBitmapFilter;

public class OpenCVEdgeDetectionCanny extends OpenCVNegative {
	// Init resources
    protected Mat mGraySubmat = new Mat();

@Override
public IBitmapFilter applyfilter() {
    
	// Convert bitmap to mat
	Utils.bitmapToMat(lastBitmap, mIntermediateMat);
	mGraySubmat = mIntermediateMat.submat(0, lastBitmap.getHeight(), 0, lastBitmap.getWidth());
	// Apply canny filter
    Imgproc.Canny(mGraySubmat, mIntermediateMat, 70, 100);
    // Convert to RGB again
    Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
    try {
        Utils.matToBitmap(mRGBA, this.lastBitmap);
    } catch(Exception e) {
        Log.e(this.getClass().getCanonicalName(), "Could not convert image to bitmap: " + e.getMessage());
    }
	
    
	return this;
}

	protected void finalize() throws Throwable {
		super.finalize();
		// Release resources
		mGraySubmat.release();
		mGraySubmat = null;
	}
	
	@Override
	public String toString() {
		return "Detect Edges";
	}
}
