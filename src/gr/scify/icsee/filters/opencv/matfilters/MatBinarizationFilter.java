package gr.scify.icsee.filters.opencv.matfilters;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;

import java.util.Locale;

import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IMatFilter;

public class MatBinarizationFilter extends MatEdgeDetectionCannyFilter {
	int iThreshold = 128;
	
	@Override
	public void decreaseParameter() {
		iThreshold -= (iThreshold > 5) ? 5 : 0;
	}
	
	@Override
	public void increaseParameter() {
		iThreshold += (iThreshold < 250) ? 5 : 0;		
	}

	@Override
	public IMatFilter applyfilter() {
	    // Convert to grayscale
	    Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);
		
		// Apply threshold
	    threshold(mIntermediateMat, mIntermediateMat, iThreshold, 255, THRESH_BINARY);
	    
	    // Convert to color
	    Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
	    
	    return this;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "Binarization %d", iThreshold);
	}	
}
