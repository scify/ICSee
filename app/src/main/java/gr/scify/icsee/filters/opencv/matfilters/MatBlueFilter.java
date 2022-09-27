package gr.scify.icsee.filters.opencv.matfilters;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import gr.scify.icsee.filters.IMatFilter;

public class MatBlueFilter implements IMatFilter {
    protected Mat mIntermediateMat = new Mat();
    protected Mat mRGBA = new Mat();
    protected Mat mB = new Mat();
    protected Mat mG = new Mat();
    protected Mat mR = new Mat();


    @Override
    public IMatFilter applyfilter() {
        // Convert to grayscale
        Imgproc.cvtColor(mRGBA, mB, Imgproc.COLOR_BGRA2GRAY, 1);

        // Init split channels list
        List<Mat> lSrc = new ArrayList<Mat>();
        // Zero-out other channels
        mG = Mat.zeros(mRGBA.rows(), mRGBA.cols(), mB.type());
        mR = Mat.zeros(mRGBA.rows(), mRGBA.cols(), mB.type());
        lSrc.add(mR);
        lSrc.add(mG);
        lSrc.add(mB);

        Core.merge(lSrc, mRGBA);
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
        mB.release();
        mB = null;
        mG.release();
        mG = null;
        mR.release();
        mR = null;
        mRGBA.release();
        mRGBA = null;
        mIntermediateMat.release();
        mIntermediateMat = null;
    }

    @Override
    public String toString() {
        return "Blue filter";
    }

    @Override
    public void decreaseParameter() {
        // Ignore
    }

    @Override
    public void increaseParameter() {
        // Ignore
    }
}
