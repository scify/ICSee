package gr.scify.icsee.filters.cpu;

import gr.scify.icsee.filters.IBitmapFilter;
import android.graphics.Bitmap;
import android.graphics.Color;

public class InvertGrayScaleFilter extends GrayScaleFilter {

		@Override
		public IBitmapFilter applyfilter() 
		{
			// Set progress title
			setProgressTitle("Invert Filter");
			
			// Use current bitmap 
			Bitmap b = lastBitmap;
			// For every pixel
			for (int x=0; x < b.getWidth(); x++) {
				for (int y=0; (y < b.getHeight()); y++) {
					// Read RGB color
					int cPixCol = b.getPixel(x, y);
					// Convert to HSV
					float[] cHSVPixCol = new float[3];
					Color.colorToHSV(cPixCol, cHSVPixCol);
					// Invert hue (for multi-color pictures)
					// cHSVPixCol[0] = 1 - cHSVPixCol[0];
					cHSVPixCol[2] = 1 - cHSVPixCol[2];
					// Set color to picture
					b.setPixel(x, y, Color.HSVToColor(cHSVPixCol));
				}

				setProgress((float)x / b.getWidth());
			}
			
			// Update last bitmap filtered
			lastBitmap = b;
			// Return bitmap
			return this;
		}


}
