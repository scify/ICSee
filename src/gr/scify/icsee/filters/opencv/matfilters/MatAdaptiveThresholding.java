package gr.scify.icsee.filters.opencv.matfilters;

import gr.scify.icsee.filters.IMatFilter;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class MatAdaptiveThresholding extends MatBinarizationFilter {
	@Override
	public IMatFilter applyfilter() {
		
		// Divide the image by its morphologically closed counterpart
//		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(19,19));
//		Mat closed = new Mat();
//		Imgproc.morphologyEx(this.mRGBA, closed, Imgproc.MORPH_CLOSE, kernel);
//
//		this.mRGBA.convertTo(this.mRGBA, CvType.CV_32F); // divide requires floating-point
//		Core.divide(this.mRGBA, closed, this.mRGBA, 1, CvType.CV_32F);
//		Core.normalize(this.mRGBA, this.mRGBA, 0, 255, Core.NORM_MINMAX);
		
		Imgproc.cvtColor(mRGBA, mIntermediateMat, Imgproc.COLOR_BGRA2GRAY, 1);
		Log.w("To Gray", "OK");

		Mat temp = new Mat();
		Mat image = mIntermediateMat;

		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(9,9));		
		Log.w("Kernel", "OK");
		Imgproc.resize(image, temp, new Size(image.rows()/4, image.cols()/4));
		Log.w("Resize reduce", "OK");
		Imgproc.morphologyEx(temp, temp, Imgproc.MORPH_CLOSE, kernel);
		Imgproc.resize(temp, temp, new Size(image.rows(), image.cols()));
		Log.w("Resize increase", "OK");

//		Core.divide(image, temp, temp, 1, CvType.CV_32F); // temp will now have type CV_32F
//		Log.w("Divide", "OK");
//		Core.normalize(temp, image, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
//		Log.w("Normalize", "OK");

		Imgproc.threshold(image, image, -1, 255, 
		    Imgproc.THRESH_BINARY_INV+Imgproc.THRESH_OTSU);		
		Log.w("Threshold", "OK");
		
		// Return to RGBA
	    Imgproc.cvtColor(mIntermediateMat, mRGBA, Imgproc.COLOR_GRAY2BGRA, 4);
		Log.w("Back to RGBA", "OK");
	    

		return this;
	}
	
	@Override
	public String toString() {
		return "Adaptive Threshold";
	}
}
