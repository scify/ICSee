package gr.scify.icsee.filters.opencv.matfilters;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import gr.scify.icsee.filters.IMatFilter;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;
import static org.opencv.imgproc.Imgproc.threshold;

public class MatWhiteRedFilter extends MatBinarizationFilter {
    @Override
    public IMatFilter applyfilter() {
        // Convert to grayscale
        Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);

        // Apply threshold
        threshold(mIntermediateMat, mIntermediateMat, iThreshold, 255, THRESH_BINARY);

        // Create mask that shows which pixels should be turned to red later
        Mat mask = new Mat();
        threshold(mIntermediateMat, mask, 1, 1, THRESH_BINARY_INV);

        // Convert to color
        Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);

        // Change all pixels that should be red to red
        mRGBA.setTo(new Scalar(255, 0, 0, 0), mask);


        return this;
    }

    @Override
     public String toString() {
        return "White and Red";
    }
}
