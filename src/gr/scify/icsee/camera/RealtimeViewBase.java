package gr.scify.icsee.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import java.util.Date;
import java.util.List;

public abstract class RealtimeViewBase extends GLSurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = RealtimeViewBase.class.getCanonicalName();

    private VideoCapture        mCamera;
    private SurfaceHolder       mHolder;
    private int                 mFrameWidth;
    private int                 mFrameHeight;
	private boolean 			bAnalysisOngoing = false;

	public VideoCapture getCamera() {
		openCamera(Utils.findFrontFacingCamera());
        return mCamera;
	}

    public RealtimeViewBase(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        Log.d(TAG, "Instantiated new " + this.getClass());
    }

    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }

//    private int initTexture() {
//    	int[] textures = new int[1];
//    	// generate one texture pointer and bind it as an external texture.
//    	GLES20.glGenTextures(1, textures, 0);
//    	GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
//    	// No mip-mapping with camera source.
//    	GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
//    	        GL10.GL_TEXTURE_MIN_FILTER,
//    	                        GL10.GL_LINEAR);        
//    	GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
//    	        GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//    	// Clamp to edge is only option.
//    	GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
//    	        GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//    	GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
//    	        GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
//    	 
//    	 
//    	int texture_id = textures[0];
//    	return texture_id;
//    }
    
    
    public boolean openCamera(int iCamID) {
        Log.i(TAG, "openCamera");
        synchronized (this) {
	        releaseCamera();

	        
	        
	        mCamera = new VideoCapture(iCamID);
	        if (!mCamera.isOpened()) {
	            mCamera.release();
	            mCamera = null;
	            Log.e(TAG, "Failed to open native camera");
	            return false;
	        }
	        else
	        {
	        	try {
	    	        mCamera.set(Highgui.CV_CAP_PROP_ANDROID_FLASH_MODE, Highgui.CV_CAP_ANDROID_FLASH_MODE_TORCH);
	    	        mCamera.set(Highgui.CV_CAP_PROP_ANDROID_FOCUS_MODE, Highgui.CV_CAP_ANDROID_FOCUS_MODE_INFINITY);
	        	}
	        	catch (Exception e) {
	        		// Ignore
	        	}
	        }
	    }
        return true;
    }
    
    public void releaseCamera() {
        Log.i(TAG, "releaseCamera");
        synchronized (this) {
	        if (mCamera != null) {
	                mCamera.release();
	                mCamera = null;
            }
        }
    }
    
    public void setupCamera(int width, int height) {
        Log.i(TAG, "setupCamera("+width+", "+height+")");
        if (mCamera != null && mCamera.isOpened()) {
            List<Size> sizes = mCamera.getSupportedPreviewSizes();
            int mFrameWidth = width;
            int mFrameHeight = height;

            // selecting optimal camera preview size
            {
                double minDiff = Double.MAX_VALUE;
                for (Size size : sizes) {
                    if (Math.abs(size.height - height) < minDiff) {
                        mFrameWidth = (int) size.width;
                        mFrameHeight = (int) size.height;
                        minDiff = Math.abs(size.height - height);
                    }
                }
            }

            mCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, mFrameWidth);
            mCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, mFrameHeight);
        }
    }
    
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        setupCamera(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        // Check if already analyzing
        if (!bAnalysisOngoing)
        	(new Thread(this)).start(); // Only start if not already analyzing
        else
        	Log.i(TAG, "Skipped frame...");
        
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        releaseCamera();
    }

    protected abstract Bitmap processFrame(VideoCapture capture);

    public void run() {
    	Date dLast = new Date();
        Log.i(TAG, "Starting processing thread");
        while (true) {
        	if (new Date().getTime() - dLast.getTime() < 1000)
        		continue;
        	dLast = new Date();
        	
            Bitmap bmp = null;

            synchronized (mCamera) {
                if (mCamera == null)
                    break;

                if (!mCamera.grab()) {
                    Log.e(TAG, "mCamera.grab() failed");
                    break;
                }				
			}

            bAnalysisOngoing = true;
            bmp = processFrame(mCamera);
            bAnalysisOngoing = false;

            if (bmp != null) {
                Canvas canvas = mHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawBitmap(bmp, (canvas.getWidth() - bmp.getWidth()) / 2, (canvas.getHeight() - bmp.getHeight()) / 2, null);
                    mHolder.unlockCanvasAndPost(canvas);
                }
                bmp.recycle();
                bmp = null;
            }
        }

        Log.i(TAG, "Finishing processing thread");
    }
}