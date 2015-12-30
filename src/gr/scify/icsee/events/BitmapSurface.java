package gr.scify.icsee.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class BitmapSurface extends SurfaceView implements Callback {
	Bitmap bmToPaint;
	BitmapPainterThread bptPainter;
	private Paint mLocalPaint;
	
	protected void commonInit() {
		this.getHolder().addCallback(this);

	    mLocalPaint = new Paint();
	    mLocalPaint.setAntiAlias(true);
	    mLocalPaint.setFilterBitmap(true);
	    mLocalPaint.setDither(true);
		
	    setWillNotDraw(false);
	}

	public BitmapSurface(Context context, AttributeSet as, int defStyle) {
		super(context, as, defStyle);
		commonInit();
	}

	public BitmapSurface(Context context, AttributeSet as) {
		super(context, as);
		commonInit();
	}
	
	public BitmapSurface(Context context, Bitmap bmToPaint) {
		super(context);
		this.bmToPaint = bmToPaint;

		commonInit();
	}
	
	public void setBitmap(Bitmap bmToPaint) {
		if (this.bmToPaint != bmToPaint) {
			this.bmToPaint = bmToPaint;
			if (getHolder().getSurface().isValid())
				postInvalidate();
		}
	}
	
	public Bitmap getBitmap() {
		return bmToPaint;
	}
	
	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		
		if (this.bmToPaint != null) {
//		    Canvas overlayCanvas = holder.lockCanvas();
		    
		    if (c != null) {
		        try {
		    		c.drawBitmap(bmToPaint, 0, 0, mLocalPaint);
//		        	overlayCanvas.drawBitmap(bmToPaint, 0, 0, p);										
		        } finally {
		            // Post changes
//		        	holder.unlockCanvasAndPost(overlayCanvas);									            	
		        }
		    }
		}
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		bptPainter = new BitmapPainterThread(holder, getContext(), this);
		bptPainter.setRunning(true);
		bptPainter.start();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		bptPainter.setRunning(false);
		boolean retry = true;
		while (retry) {
			try
			{
				bptPainter.join();
				retry = false;
			}
			catch(Exception e)
			{
				Log.v("Exception Occured", e.getMessage());
			}
		}
	}
}


