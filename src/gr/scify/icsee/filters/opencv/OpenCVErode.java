package gr.scify.icsee.filters.opencv;

import gr.scify.icsee.filters.IBitmapFilter;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class OpenCVErode extends OpenCVNegative {
	@Override
	public IBitmapFilter applyfilter() {
		synchronized (this.lastBitmap) {
			// Convert bitmap to mat
			Utils.bitmapToMat(lastBitmap, mRGBA);
			double erosion_size = 5.0;
			// Apply erode
			Mat kernel = Imgproc.getStructuringElement( Imgproc.MORPH_ERODE,
                    new Size( 2*erosion_size + 1, 2*erosion_size+1 ),
                    new Point( erosion_size, erosion_size ) );

			Imgproc.erode(mRGBA, mIntermediateMat, kernel);
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
		return "Erode";
	}

}
