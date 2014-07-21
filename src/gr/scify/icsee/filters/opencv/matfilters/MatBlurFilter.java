package gr.scify.icsee.filters.opencv.matfilters;

import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IMatFilter;

public class MatBlurFilter extends MatBlueFilter {
	protected int iWindowSize = 3;
	@Override
	public IMatFilter applyfilter() {
	    // Apply blur
	    Imgproc.blur(mRGBA, mIntermediateMat, new Size(iWindowSize,iWindowSize));
	    mIntermediateMat.copyTo(mRGBA);
	    
		return this;
	}
	
	@Override
	public void decreaseParameter() {
		iWindowSize -= (iWindowSize > 1) ? 2 : 0;
	}
	
	@Override
	public void increaseParameter() {
		iWindowSize += (iWindowSize < 15) ? 2 : 0;
	}

	@Override
	public String toString() {
		return "Blur " + String.valueOf(iWindowSize);
	}
}
