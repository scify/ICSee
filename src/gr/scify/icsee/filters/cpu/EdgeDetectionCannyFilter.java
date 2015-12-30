package gr.scify.icsee.filters.cpu;

//import java.awt.image.BufferedImage;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import gr.scify.icsee.filters.IBitmapFilter;

/**
 * <p><em>This software has been released into the public domain.
 * <strong>Please read the notes in this source file for additional information.
 * </strong></em></p>
 * 
 * <p>This class provides a configurable implementation of the Canny edge
 * detection algorithm. This classic algorithm has a number of shortcomings,
 * but remains an effective tool in many scenarios. <em>This class is designed
 * for single threaded use only.</em></p>
 * 
 * <p>Sample usage:</p>
 * 
 * <pre><code>
 * //create the detector
 * CannyEdgeDetector detector = new CannyEdgeDetector();
 * //adjust its parameters as desired
 * detector.setLowThreshold(0.5f);
 * detector.setHighThreshold(1f);
 * //apply it to an image
 * detector.setSourceImage(frame);
 * detector.process();
 * BufferedImage edges = detector.getEdgesImage();
 * </code></pre>
 * 
 * <p>For a more complete understanding of this edge detector's parameters
 * consult an explanation of the algorithm.</p>
 * 
 * @author Original: Tom Gibara, Slight changes: SciFY
 *
 */

public class EdgeDetectionCannyFilter extends GrayScaleFilter {
		// statics
		
		private final static float GAUSSIAN_CUT_OFF = 0.005f;
		private final static float MAGNITUDE_SCALE = 100F;
		private final static float MAGNITUDE_LIMIT = 1000F;
		private final static int MAGNITUDE_MAX = (int) (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);

		// fields
		
		private int height;
		private int width;
		private int picsize;
		private int[] data;
		private int[] magnitude;
		private Bitmap sourceImage;
		private Bitmap edgesImage;
		
		private float gaussianKernelRadius;
		private float lowThreshold;
		private float highThreshold;
		private int gaussianKernelWidth;
		private boolean contrastNormalized;

		private float[] xConv;
		private float[] yConv;
		private float[] xGradient;
		private float[] yGradient;
		
		// constructors
		
		/**
		 * Constructs a new detector with default parameters.
		 */
		
		public EdgeDetectionCannyFilter() {
			lowThreshold = 2.5f;
			highThreshold = 7.5f;
			gaussianKernelRadius = 2f;
			gaussianKernelWidth = 16;
			contrastNormalized = false;
		}

		// accessors
		
		/**
		 * The image that provides the luminance data used by this detector to
		 * generate edges.
		 * 
		 * @return the source image, or null
		 */
		
		public Bitmap getSourceImage() {
			return sourceImage;
		}
		
		/**
		 * Specifies the image that will provide the luminance data in which edges
		 * will be detected. A source image must be set before the process method
		 * is called.
		 *  
		 * @param image a source of luminance data
		 */
		
		public void setSourceImage(Bitmap image) {
			sourceImage = image;
		}

		/**
		 * Obtains an image containing the edges detected during the last call to
		 * the process method. The buffered image is an opaque image of type
		 * BufferedImage.TYPE_INT_ARGB in which edge pixels are white and all other
		 * pixels are black.
		 * 
		 * @return an image containing the detected edges, or null if the process
		 * method has not yet been called.
		 */
		
		public Bitmap getEdgesImage() {
			return edgesImage;
		}
	 
		/**
		 * Sets the edges image. Calling this method will not change the operation
		 * of the edge detector in any way. It is intended to provide a means by
		 * which the memory referenced by the detector object may be reduced.
		 * 
		 * @param edgesImage expected (though not required) to be null
		 */
		
		public void setEdgesImage(Bitmap edgesImage) {
			this.edgesImage = edgesImage;
		}

		/**
		 * The low threshold for hysteresis. The default value is 2.5.
		 * 
		 * @return the low hysteresis threshold
		 */
		
		public float getLowThreshold() {
			return lowThreshold;
		}
		
		/**
		 * Sets the low threshold for hysteresis. Suitable values for this parameter
		 * must be determined experimentally for each application. It is nonsensical
		 * (though not prohibited) for this value to exceed the high threshold value.
		 * 
		 * @param threshold a low hysteresis threshold
		 */
		
		public void setLowThreshold(float threshold) {
			if (threshold < 0) throw new IllegalArgumentException();
			lowThreshold = threshold;
		}
	 
		/**
		 * The high threshold for hysteresis. The default value is 7.5.
		 * 
		 * @return the high hysteresis threshold
		 */
		
		public float getHighThreshold() {
			return highThreshold;
		}
		
		/**
		 * Sets the high threshold for hysteresis. Suitable values for this
		 * parameter must be determined experimentally for each application. It is
		 * nonsensical (though not prohibited) for this value to be less than the
		 * low threshold value.
		 * 
		 * @param threshold a high hysteresis threshold
		 */
		
		public void setHighThreshold(float threshold) {
			if (threshold < 0) throw new IllegalArgumentException();
			highThreshold = threshold;
		}

		/**
		 * The number of pixels across which the Gaussian kernel is applied.
		 * The default value is 16.
		 * 
		 * @return the radius of the convolution operation in pixels
		 */
		
		public int getGaussianKernelWidth() {
			return gaussianKernelWidth;
		}
		
		/**
		 * The number of pixels across which the Gaussian kernel is applied.
		 * This implementation will reduce the radius if the contribution of pixel
		 * values is deemed negligable, so this is actually a maximum radius.
		 * 
		 * @param gaussianKernelWidth a radius for the convolution operation in
		 * pixels, at least 2.
		 */
		
		public void setGaussianKernelWidth(int gaussianKernelWidth) {
			if (gaussianKernelWidth < 2) throw new IllegalArgumentException();
			this.gaussianKernelWidth = gaussianKernelWidth;
		}

		/**
		 * The radius of the Gaussian convolution kernel used to smooth the source
		 * image prior to gradient calculation. The default value is 16.
		 * 
		 * @return the Gaussian kernel radius in pixels
		 */
		
		public float getGaussianKernelRadius() {
			return gaussianKernelRadius;
		}
		
		/**
		 * Sets the radius of the Gaussian convolution kernel used to smooth the
		 * source image prior to gradient calculation.
		 * 
		 * @return a Gaussian kernel radius in pixels, must exceed 0.1f.
		 */
		
		public void setGaussianKernelRadius(float gaussianKernelRadius) {
			if (gaussianKernelRadius < 0.1f) throw new IllegalArgumentException();
			this.gaussianKernelRadius = gaussianKernelRadius;
		}
		
		/**
		 * Whether the luminance data extracted from the source image is normalized
		 * by linearizing its histogram prior to edge extraction. The default value
		 * is false.
		 * 
		 * @return whether the contrast is normalized
		 */
		
		public boolean isContrastNormalized() {
			return contrastNormalized;
		}
		
		/**
		 * Sets whether the contrast is normalized
		 * @param contrastNormalized true if the contrast should be normalized,
		 * false otherwise
		 */
		
		public void setContrastNormalized(boolean contrastNormalized) {
			this.contrastNormalized = contrastNormalized;
		}
		
		// methods
		
		public void process() {
			width = sourceImage.getWidth();
			height = sourceImage.getHeight();
			picsize = width * height;
			initArrays();
			readLuminance();
			if (contrastNormalized) normalizeContrast();
			// Step 1
			setProgressTitle("Edge Detection 1/4");
			computeGradients(gaussianKernelRadius, gaussianKernelWidth);
			
			// Step 2
			setProgressTitle("Edge Detection Step 2/4");
			int low = Math.round(lowThreshold * MAGNITUDE_SCALE);
			int high = Math.round( highThreshold * MAGNITUDE_SCALE);
			performHysteresis(low, high);
			
			// Step 3
			setProgressTitle("Edge Detection 3/4");
			thresholdEdges();
			
			// Final Step 4
			setProgressTitle("Edge Detection 4/4");
			writeEdges(data);
			
		}
	 
		// private utility methods
		
		private void initArrays() {
			if (data == null || picsize != data.length) {
				data = new int[picsize];
				magnitude = new int[picsize];

				xConv = new float[picsize];
				yConv = new float[picsize];
				xGradient = new float[picsize];
				yGradient = new float[picsize];
			}
		}
		
		//NOTE: The elements of the method below (specifically the technique for
		//non-maximal suppression and the technique for gradient computation)
		//are derived from an implementation posted in the following forum (with the
		//clear intent of others using the code):
		//  http://forum.java.sun.com/thread.jspa?threadID=546211&start=45&tstart=0
		//My code effectively mimics the algorithm exhibited above.
		//Since I don't know the providence of the code that was posted it is a
		//possibility (though I think a very remote one) that this code violates
		//someone's intellectual property rights. If this concerns you feel free to
		//contact me for an alternative, though less efficient, implementation.
		
		private void computeGradients(float kernelRadius, int kernelWidth) {
			//generate the gaussian convolution masks
			float kernel[] = new float[kernelWidth];
			float diffKernel[] = new float[kernelWidth];
			int kwidth;
			for (kwidth = 0; kwidth < kernelWidth; kwidth++) {
				float g1 = gaussian(kwidth, kernelRadius);
				if (g1 <= GAUSSIAN_CUT_OFF && kwidth >= 2) break;
				float g2 = gaussian(kwidth - 0.5f, kernelRadius);
				float g3 = gaussian(kwidth + 0.5f, kernelRadius);
				kernel[kwidth] = (g1 + g2 + g3) / 3f / (2f * (float) Math.PI * kernelRadius * kernelRadius);
				diffKernel[kwidth] = g3 - g2;
			}

			int initX = kwidth - 1;
			int maxX = width - (kwidth - 1);
			int initY = width * (kwidth - 1);
			int maxY = width * (height - (kwidth - 1));
			
			//perform convolution in x and y directions
			for (int x = initX; x < maxX; x++) {
				for (int y = initY; y < maxY; y += width) {
					int index = x + y;
					float sumX = data[index] * kernel[0];
					float sumY = sumX;
					int xOffset = 1;
					int yOffset = width;
					for(; xOffset < kwidth ;) {
						sumY += kernel[xOffset] * (data[index - yOffset] + data[index + yOffset]);
						sumX += kernel[xOffset] * (data[index - xOffset] + data[index + xOffset]);
						yOffset += width;
						xOffset++;
					}
					
					yConv[index] = sumY;
					xConv[index] = sumX;
				}
				// Update progress
				setProgress(0.10 * x / width);	 
			}
	 
			for (int x = initX; x < maxX; x++) {
				for (int y = initY; y < maxY; y += width) {
					float sum = 0f;
					int index = x + y;
					for (int i = 1; i < kwidth; i++)
						sum += diffKernel[i] * (yConv[index - i] - yConv[index + i]);
	 
					xGradient[index] = sum;
				}
	 
			}

			for (int x = kwidth; x < width - kwidth; x++) {
				for (int y = initY; y < maxY; y += width) {
					float sum = 0.0f;
					int index = x + y;
					int yOffset = width;
					for (int i = 1; i < kwidth; i++) {
						sum += diffKernel[i] * (xConv[index - yOffset] - xConv[index + yOffset]);
						yOffset += width;
					}
	 
					yGradient[index] = sum;
				}
	 
				// Update progress
				setProgress(0.10 + 0.10 * x / width);
			}
	 
			initX = kwidth;
			maxX = width - kwidth;
			initY = width * kwidth;
			maxY = width * (height - kwidth);
			for (int x = initX; x < maxX; x++) {
				for (int y = initY; y < maxY; y += width) {
					int index = x + y;
					int indexN = index - width;
					int indexS = index + width;
					int indexW = index - 1;
					int indexE = index + 1;
					int indexNW = indexN - 1;
					int indexNE = indexN + 1;
					int indexSW = indexS - 1;
					int indexSE = indexS + 1;
					
					float xGrad = xGradient[index];
					float yGrad = yGradient[index];
					float gradMag = hypot(xGrad, yGrad);

					//perform non-maximal supression
					float nMag = hypot(xGradient[indexN], yGradient[indexN]);
					float sMag = hypot(xGradient[indexS], yGradient[indexS]);
					float wMag = hypot(xGradient[indexW], yGradient[indexW]);
					float eMag = hypot(xGradient[indexE], yGradient[indexE]);
					float neMag = hypot(xGradient[indexNE], yGradient[indexNE]);
					float seMag = hypot(xGradient[indexSE], yGradient[indexSE]);
					float swMag = hypot(xGradient[indexSW], yGradient[indexSW]);
					float nwMag = hypot(xGradient[indexNW], yGradient[indexNW]);
					float tmp;
					/*
					 * An explanation of what's happening here, for those who want
					 * to understand the source: This performs the "non-maximal
					 * supression" phase of the Canny edge detection in which we
					 * need to compare the gradient magnitude to that in the
					 * direction of the gradient; only if the value is a local
					 * maximum do we consider the point as an edge candidate.
					 * 
					 * We need to break the comparison into a number of different
					 * cases depending on the gradient direction so that the
					 * appropriate values can be used. To avoid computing the
					 * gradient direction, we use two simple comparisons: first we
					 * check that the partial derivatives have the same sign (1)
					 * and then we check which is larger (2). As a consequence, we
					 * have reduced the problem to one of four identical cases that
					 * each test the central gradient magnitude against the values at
					 * two points with 'identical support'; what this means is that
					 * the geometry required to accurately interpolate the magnitude
					 * of gradient function at those points has an identical
					 * geometry (upto right-angled-rotation/reflection).
					 * 
					 * When comparing the central gradient to the two interpolated
					 * values, we avoid performing any divisions by multiplying both
					 * sides of each inequality by the greater of the two partial
					 * derivatives. The common comparand is stored in a temporary
					 * variable (3) and reused in the mirror case (4).
					 * 
					 */
					if (xGrad * yGrad <= (float) 0 /*(1)*/
						? Math.abs(xGrad) >= Math.abs(yGrad) /*(2)*/
							? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * neMag - (xGrad + yGrad) * eMag) /*(3)*/
								&& tmp > Math.abs(yGrad * swMag - (xGrad + yGrad) * wMag) /*(4)*/
							: (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * neMag - (yGrad + xGrad) * nMag) /*(3)*/
								&& tmp > Math.abs(xGrad * swMag - (yGrad + xGrad) * sMag) /*(4)*/
						: Math.abs(xGrad) >= Math.abs(yGrad) /*(2)*/
							? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * seMag + (xGrad - yGrad) * eMag) /*(3)*/
								&& tmp > Math.abs(yGrad * nwMag + (xGrad - yGrad) * wMag) /*(4)*/
							: (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * seMag + (yGrad - xGrad) * sMag) /*(3)*/
								&& tmp > Math.abs(xGrad * nwMag + (yGrad - xGrad) * nMag) /*(4)*/
						) {
						magnitude[index] = gradMag >= MAGNITUDE_LIMIT ? MAGNITUDE_MAX : (int) (MAGNITUDE_SCALE * gradMag);
						//NOTE: The orientation of the edge is not employed by this
						//implementation. It is a simple matter to compute it at
						//this point as: Math.atan2(yGrad, xGrad);
					} else {
						magnitude[index] = 0;
					}
				}
				// Update progress bar
				setProgress(0.2 + 0.2 * x / width);
			}
		}
	 
		//NOTE: It is quite feasible to replace the implementation of this method
		//with one which only loosely approximates the hypot function. I've tested
		//simple approximations such as Math.abs(x) + Math.abs(y) and they work fine.
		private float hypot(float x, float y) {
			return (float) Math.hypot(x, y);
		}
	 
		private float gaussian(float x, float sigma) {
			return (float) Math.exp(-(x * x) / (2f * sigma * sigma));
		}
	 
		private void performHysteresis(int low, int high) {
			//NOTE: this implementation reuses the data array to store both
			//luminance data from the image, and edge intensity from the processing.
			//This is done for memory efficiency, other implementations may wish
			//to separate these functions.
			Arrays.fill(data, 0);
	 
			int offset = 0;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (data[offset] <= 0 && magnitude[offset] >= high) {
						follow(x, y, offset, low);
					}
					offset++;
				}
				setProgress(0.4  + 0.2 * (float)x / width);
			}
	 	}
	 
		private void follow(int x1, int y1, int i1, int threshold) {
			
			// TODO: Replace with for loop to avoid overflow
			
			int x0 = x1 == 0 ? x1 : x1 - 1;
			int x2 = x1 == width - 1 ? x1 : x1 + 1;
			int y0 = y1 == 0 ? y1 : y1 - 1;
			int y2 = y1 == height -1 ? y1 : y1 + 1;
			
			Set<Integer[]> siToVisit = new LinkedHashSet<Integer[]>();
			Integer[] toFirstAdd = {x0, x2, y0, y2 };
			siToVisit.add(toFirstAdd);
			
			// DEBUG LINES
//			int iCnt = 0;
			//////////////
			Iterator<Integer[]> liIter = siToVisit.iterator();
			while (liIter.hasNext()) {
				// Get next item
				Integer[] iaCur = liIter.next();
				// DEBUG LINES
//				if (iCnt++ % 1000 == 0) {
//					Log.d(this.getClass().getCanonicalName(), "" + iCnt + " items remaining...");
//					if (iCnt > picsize)
//						return;
//				}
				//////////////
				x0 = iaCur[0];
				x2 = iaCur[1];
				y0 = iaCur[2];
				y2 = iaCur[3];
				data[i1] = magnitude[i1];
				for (int x = x0; x <= x2; x++) {
					for (int y = y0; y <= y2; y++) {
						int i2 = x + y * width;
						// If in edge
						if ((y != y1 || x != x1)
							&& data[i2] == 0 
							&& magnitude[i2] >= threshold) {
//							follow(x, y, i2, threshold);
//							return;
							// Calculate values
							int x0p = x == 0 ? x : x - 1;
							int x2p = x == width - 1 ? x : x + 1;
							int y0p = y == 0 ? y : y - 1;
							int y2p = y == height -1 ? y : y + 1;
							// Visit later
							Integer[] toAdd = {x0p, x2p, y0p, y2p };
							siToVisit.add(toAdd);
//							break;
						}
					}
				}				
			}
		}

		private void thresholdEdges() {
			for (int i = 0; i < picsize; i++) {
				data[i] = data[i] > 0 ? -1 : 0xff000000;
				// Update progress bar
				if (i % width == 0)
					setProgress(0.6 + 0.2 * ((float)i / width) / height );					
			}
		}
		
		private int luminance(float r, float g, float b) {
			// ORIGINAL: return Math.round(0.299f * r + 0.587f * g + 0.114f * b);
			// From Wikipedia
			return Math.round(0.2126f * r + 0.7152f * g + 0.0722f * b); //  ITU-R BT.709 primaries' color space used (?)
		}
		
		private void readLuminance() {
			// Applied on an RGB image
			for (int iX = 0; iX < width; iX++)
				for (int iY = 0; iY < height; iY++) {
					// Read RGB color
					int cPixCol = sourceImage.getPixel(iX, iY);
	                int b = cPixCol & 0xff;
	                int g = (cPixCol & 0xff00) / 0x100;
	                int r = (cPixCol & 0xff0000) / 0x10000;
					// Get luminance
					data[iY*width + iX] = luminance(r, g, b);
				}
			
		}
	 
		private void normalizeContrast() {
			int[] histogram = new int[256];
			for (int i = 0; i < data.length; i++) {
				histogram[data[i]]++;
			}
			int[] remap = new int[256];
			int sum = 0;
			int j = 0;
			for (int i = 0; i < histogram.length; i++) {
				sum += histogram[i];
				int target = sum*255/picsize;
				for (int k = j+1; k <=target; k++) {
					remap[k] = i;
				}
				j = target;
			}
			
			for (int i = 0; i < data.length; i++) {
				data[i] = remap[data[i]];
			}
		}
		
		private void writeEdges(int pixels[]) {
			edgesImage = lastBitmap;
			for (int iX = 0; iX < width; iX++) {
				for (int iY = 0; iY < height; iY++) {
					//edgesImage.setPixels(data, 0, 0, 0, 0, width, height);
					if (data[iY * width + iX] > 0)
						Log.d(getClass().getCanonicalName(), "Found non-zero pixel...");
					edgesImage.setPixel(iX, iY, data[iY * width + iX]);
				}
				setProgress(0.8 + 0.2 * (float)iX / width);
			}
		}
	 

		@Override
		public IBitmapFilter applyfilter() {
			setLowThreshold(0.5f);
			setHighThreshold(1.0f);
			setSourceImage(lastBitmap);
			process();
			
			return this;
		}
}
