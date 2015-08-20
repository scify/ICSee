package gr.scify.icsee.filters.opencv.matfilters;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IMatFilter;

import static org.opencv.imgproc.Imgproc.threshold;

public class MatBlueYellowFilter extends MatBinarizationFilter {
    @Override
    public IMatFilter applyfilter() {
        // Convert to grayscale
        Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);

        // Apply threshold
        threshold(mIntermediateMat, mIntermediateMat, iThreshold, 255, Imgproc.THRESH_BINARY_INV);

        // Create mask that shows which pixels should be turned to yellow later
        Mat yellowMask = new Mat();
        Mat blueMask = new Mat();
        threshold(mIntermediateMat, yellowMask, 1, 1, Imgproc.THRESH_BINARY);
        threshold(mIntermediateMat, blueMask, 1, 1, Imgproc.THRESH_BINARY_INV);

        // Convert to color
        Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);

        // Change all pixels that should be yellow to yellow
        mRGBA.setTo(new Scalar(255, 255, 0, 0), yellowMask);

        // Change all pixels that should be blue to blue
        mRGBA.setTo(new Scalar(0, 0, 255, 0), blueMask);

        return this;
    }

    @Override
    public String toString() {
        return "Black and Yellow";
    }
}
