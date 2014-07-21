package gr.scify.icsee.filters.opencv.matfilters;

import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IMatFilter;

public class MatSmoothFilterMedian extends MatBlurFilter {
	@Override
	public IMatFilter applyfilter() {
		// Apply median blurring
		Imgproc.medianBlur(mRGBA, mIntermediateMat, iWindowSize);
		
		// Convert back to color
		mIntermediateMat.copyTo(mRGBA);
	    
		return this;
	}
	
	@Override
	public String toString() {
		return "Smooth (Median)";
	}
}
