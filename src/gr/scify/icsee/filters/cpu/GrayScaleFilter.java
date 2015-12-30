package gr.scify.icsee.filters.cpu;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import gr.scify.icsee.filters.IBitmapFilter;

public class GrayScaleFilter implements IBitmapFilter {
	protected Bitmap lastBitmap = null;
	protected ProgressBar progressBar = null;
	protected Handler UIUpdater;
	protected TextView tvTitle;

	@Override
	public IBitmapFilter thenApply(IBitmapFilter f) {
		// Use the same bitmap to call the next filter in line
		f.setBitmap(lastBitmap);
		f.setProgressBar(progressBar);
		f.setHandler(UIUpdater);
		f.setProgressCaption(tvTitle);
		
		return f.applyfilter();
	}
	@Override
	public IBitmapFilter applyfilter() 
	{
		// Set progress title
		setProgressTitle("Grayscale Filter");
		
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
				// Remove saturation (i.e., make gray-scale)
				cHSVPixCol[1] = 0;
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

@Override
	public Bitmap getBitmap() {
		return lastBitmap;
	}

	@Override
	public void setBitmap(Bitmap bToUse) {
		lastBitmap = bToUse;
		
	}

	@Override
	public void setProgressBar(ProgressBar pb) {
		this.progressBar = pb;
	}
	
	@Override
	public void setHandler(Handler UIUpdateHandler) {
		this.UIUpdater = UIUpdateHandler;
		
	}	

	@Override
	public void setProgress(final double fProgress) {
		final ProgressBar pbArg = this.progressBar;
		if ((progressBar != null) && (this.UIUpdater != null))
			this.UIUpdater.post(new Runnable() {
				
				@Override
				public void run() {
					pbArg.setProgress((int)(fProgress * 100));
				}
			});
		Thread.yield();
	}

	@Override
	public void setProgressCaption(TextView tv) {
		this.tvTitle = tv;
		
	}	
	@Override
	public void setProgressTitle(final String sTitle) {
		final TextView tvTitleArg = this.tvTitle;
		if ((tvTitleArg != null) && (this.UIUpdater != null))
			this.UIUpdater.post(new Runnable() {
				
				@Override
				public void run() {
					tvTitleArg.setText(sTitle);
				}
			});
		Thread.yield();
	}
}
