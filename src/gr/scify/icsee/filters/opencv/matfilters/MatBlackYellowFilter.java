package gr.scify.icsee.filters.opencv.matfilters;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IMatFilter;

import static org.opencv.imgproc.Imgproc.threshold;

public class MatBlackYellowFilter extends MatBinarizationFilter {
    @Override
    public IMatFilter applyfilter() {
        // Convert to grayscale
        Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);

        // Apply threshold
        threshold(mIntermediateMat, mIntermediateMat, iThreshold, 255, Imgproc.THRESH_BINARY_INV);

        // Create mask that shows which pixels should be turned to yellow later
        Mat mask = new Mat();
        threshold(mIntermediateMat, mask, 1, 1, Imgproc.THRESH_BINARY);

        // Convert to color
        Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);

        // Change all pixels that should be yellow to yellow
        mRGBA.setTo(new Scalar(255, 255, 0, 0), mask);

        return this;
    }

    @Override
    public String toString() {
        return "Black and Yellow";
    }
}
