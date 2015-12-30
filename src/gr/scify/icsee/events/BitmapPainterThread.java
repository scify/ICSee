package gr.scify.icsee.events;

import android.content.Context;
import android.view.SurfaceHolder;

public class BitmapPainterThread extends Thread {
	SurfaceHolder surfaceHolder;
	Context context;
	boolean mRun;
	BitmapSurface mBitmapSurface;
	
	public BitmapPainterThread(SurfaceHolder sholder, Context ctx,
			BitmapSurface bsSurface)

	{
		surfaceHolder = sholder;
		context = ctx;
		mRun = false;
		mBitmapSurface = bsSurface;
	}

	void setRunning(boolean bRun)
	{
		mRun = bRun;
	}

	@Override
	public void run()
	{
		super.run();
		
		while (mRun)
		{
			mBitmapSurface.postInvalidate();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

