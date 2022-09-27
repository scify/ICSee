package gr.scify.icsee.filters.opencv.matfilters;

import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IMatFilter;

public class MatNegative extends MatEdgeDetectionCannyFilter {
	@Override
	public IMatFilter applyfilter() {
	    // Convert to grayscale
	    Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);
		
		// Subtract from 255 matrix the mRGBA
		Core.bitwise_not(mIntermediateMat, mIntermediateMat);

		// Convert back to RGBA
		Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
		return this;
	}
	
	@Override
	public String toString() {
		
		return "Negative";
	}	
}
