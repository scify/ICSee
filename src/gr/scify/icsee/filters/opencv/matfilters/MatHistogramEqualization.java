package gr.scify.icsee.filters.opencv.matfilters;

import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IMatFilter;

public class MatHistogramEqualization extends MatBlueFilter {
	@Override
	public IMatFilter applyfilter() {
	    // Convert to grayscale
	    Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);

		// Apply histogram equalization
		Imgproc.equalizeHist(mIntermediateMat, mIntermediateMat);
		
		// Convert back to color
	    Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
	    
		return this;
	}

	@Override
	public String toString() {
		
		return "HistEq";
	}
}
