package gr.scify.icsee.filters.cpu;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.TreeMap;

import gr.scify.icsee.filters.IBitmapFilter;

public class HistogramEqualizationFilter  extends GrayScaleFilter implements IBitmapFilter {

	@Override
	public IBitmapFilter applyfilter() {
		setProgressTitle("Histogram Equalization Filter - Step 1/3");
		
		// Use current bitmap 
		Bitmap b = lastBitmap;
		// Init histogram treemap (to keep ordering)
		TreeMap<Float, Float> hmHistogram = new TreeMap<Float, Float>();
		float fMaxVal = 0.0f, fMinVal = 0.0f;
		// For every pixel
		for (int x=0; x < b.getWidth(); x++) {
			for (int y=0; (y < b.getHeight()); y++) {
				// Read RGB color
				int cPixCol = b.getPixel(x, y);
				// Convert to HSV
				float[] cHSVPixCol = new float[3];
				Color.colorToHSV(cPixCol, cHSVPixCol);
				// Update histogram
				if (!hmHistogram.containsKey(cHSVPixCol[2]))
					hmHistogram.put(cHSVPixCol[2], 1.0f);
				else
					hmHistogram.put(cHSVPixCol[2], 
							hmHistogram.get(cHSVPixCol[2]) + 1.0f);
				// Update min, max value
				if (fMinVal > cHSVPixCol[2])
					fMinVal = cHSVPixCol[2];
				if (fMaxVal < cHSVPixCol[2])
					fMaxVal = cHSVPixCol[2];
			}
			int xArg = x;
			int xMax = b.getWidth();
			
			setProgress((float)0.3 * xArg / xMax);
		}
		
		setProgressTitle("Histogram Equalization Filter - Step 2/3");
		// Determine sum of values
		float fAllCounts = (float)(b.getWidth() * b.getHeight());
		// Init lookup for values
		TreeMap<Float,Float> tmLookup = new TreeMap<Float, Float>();
		Float fTotalUpToNow = 0.0f;
		// For all noted values
		for (Float f : hmHistogram.keySet()) {
			// Get CDF value
			fTotalUpToNow += hmHistogram.get(f);
			// Determine target value
			float fFinalVal = Math.min(fTotalUpToNow / fAllCounts, 1.0f) * (fMaxVal - fMinVal) + fMinVal;
			// Update lookup table
			tmLookup.put(f, fFinalVal);
		}
		setProgress(0.6);
		
		setProgressTitle("Histogram Equalization Filter - Step 3/3");
		// For every pixel
		for (int x=0; x < b.getWidth(); x++) {
			for (int y=0; (y < b.getHeight()); y++) {
				// Read RGB color
				int cPixCol = b.getPixel(x, y);
				// Convert to HSV
				float[] cHSVPixCol = new float[3];
				Color.colorToHSV(cPixCol, cHSVPixCol);
				// Update Value channel based on lookup
				cHSVPixCol[2] = tmLookup.get(cHSVPixCol[2]);
				
				// Set color to picture
				b.setPixel(x, y, Color.HSVToColor(cHSVPixCol));
			}

			int xArg = x;
			int xMax = b.getWidth();
			setProgress(0.6 + (0.4 * xArg) / xMax);
		}
		
		// Update last bitmap filtered
		lastBitmap = b;
		// Return bitmap
		return this;
		
	}

}
