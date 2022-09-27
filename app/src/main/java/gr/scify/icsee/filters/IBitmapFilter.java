package gr.scify.icsee.filters;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

public interface IBitmapFilter {
	public Bitmap getBitmap();
	public void setBitmap(Bitmap bToUse);
	public IBitmapFilter thenApply(IBitmapFilter f);
	public IBitmapFilter applyfilter();
	public void setProgressBar(ProgressBar pd);
	public void setProgressCaption(TextView tv);
	public void setHandler(Handler UIUpdateHandler);
	public void setProgressTitle(String sTitle);
	public void setProgress(double fProgress);
}
