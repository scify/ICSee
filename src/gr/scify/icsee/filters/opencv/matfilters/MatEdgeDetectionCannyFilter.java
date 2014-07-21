package gr.scify.icsee.filters.opencv.matfilters;

import gr.scify.icsee.filters.IMatFilter;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MatEdgeDetectionCannyFilter implements IMatFilter {
    protected Mat mIntermediateMat = new Mat();
    protected Mat mRGBA = new Mat();
    protected int iLow = 80;
    protected int iHigh = 90;



    @Override
    public void decreaseParameter() {
    	iLow -= (iLow > 5) ? 5 : 0;     	    	
    }
    
    @Override
    public void increaseParameter() {
    	iLow += (iLow < iHigh) ? 5 : 0;     	
    }
    
	@Override
	public IMatFilter applyfilter() {
	    // Apply canny
	    Imgproc.Canny(mRGBA, mIntermediateMat, iLow, iHigh);
	    Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
	    
		return this;
	}
	
	@Override
	public IMatFilter thenApply(IMatFilter f) {
		f.setMat(mRGBA);
		
		return f.applyfilter();
	}
	
	@Override
	public Mat getMat() {
		return mRGBA;
	}
	@Override
	public void setMat(Mat mToUse) {
		mRGBA = mToUse;
	}

    @Override
    protected void finalize() throws Throwable {
    	super.finalize();
    	    	
    	// Release resources
        mRGBA.release();
        mRGBA = null;
        mIntermediateMat.release();
        mIntermediateMat = null;
    }
	
    @Override
    public String toString() {
    	return "Edge Detection (Canny) " + String.valueOf(iLow);
    }
	

}
