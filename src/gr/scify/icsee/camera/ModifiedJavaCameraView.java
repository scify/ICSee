package gr.scify.icsee.camera;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import gr.scify.icsee.sounds.SoundPlayer;

/**
	 * This class is an implementation of the Bridge View between OpenCV and Java Camera.
	 * This class relays on the functionality available in base class and only implements
	 * required functions:
	 * connectCamera - opens Java camera and sets the PreviewCallback to be delivered.
	 * disconnectCamera - closes the camera and stops preview.
	 * When frame is delivered via callback from Camera - it processed via OpenCV to be
	 * converted to RGBA32 and then passed to the external callback for modifications if required.
	 */
	public class ModifiedJavaCameraView extends ModifiedCameraBridgeViewBase implements PreviewCallback {

	    private static final int MAGIC_TEXTURE_ID = 10;
	    private static final String TAG = "JavaCameraView";

	    private Mat mBaseMat;
	    private byte mBuffer[];
	    private Mat[] mFrameChain;
	    private int mChainIdx = 0;
	    private Thread mThread;
	    private boolean mStopThread;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	    public Camera mCamera;
	    protected Parameters pParams;
	    protected int iMinZoom, iMaxZoom;

	    private SurfaceTexture mSurfaceTexture;
	    
	    public void focusCamera() {

			mCamera.autoFocus(new Camera.AutoFocusCallback() {
				
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
                    // Play sound depending on success or failure of focus
					//Toast.makeText(getContext(), "success: " + success, Toast.LENGTH_LONG).show();
                    if (success) {
						SoundPlayer.playSound(getContext(), SoundPlayer.S6);
					} else {
						SoundPlayer.playSound(getContext(), SoundPlayer.S4);
                    }
				}
			});
		}
	    
	    public void FlashMode(){
	    	Camera.Parameters params = mCamera.getParameters();
	        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
	        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
	        mCamera.setParameters(params);
	    	
	    }
	    
	    public boolean increaseZoom() {
	    	synchronized (this) {
		    	if ((pParams.getZoom() + 2) <= iMaxZoom) {
		    		pParams.setZoom(pParams.getZoom() + 2);
			    	mCamera.setParameters(pParams);
			    	pParams = mCamera.getParameters();
		    		return true;
		    	}
		    	return false;
			}
	    }

	    public boolean decreaseZoom() {
	    	synchronized (this) {
		    	if (pParams.getZoom() >= 2) {
		    		pParams.setZoom(pParams.getZoom() - 2);
			    	mCamera.setParameters(pParams);
			    	pParams = mCamera.getParameters();
			    	return true;
		    	}
		    	return false;
			}
	    }
	    
	    public static class JavaCameraSizeAccessor implements ListItemAccessor {

	        public int getWidth(Object obj) {
	            Camera.Size size = (Camera.Size) obj;
	            return size.width;
	        }

	        public int getHeight(Object obj) {
	            Camera.Size size = (Camera.Size) obj;
	            return size.height;
	        }
	    }

	    public ModifiedJavaCameraView(Context context, int cameraId) {
	        super(context, cameraId);
	    }

	    public ModifiedJavaCameraView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        Log.d(TAG, "Java camera view ctor");
	    }

	    @TargetApi(11)
	    protected boolean initializeCamera(int width, int height) {
	        Log.d(TAG, "Initialize java camera");
	        boolean result = true;
	        synchronized (this) {
	            mCamera = null;

	            if (mCameraIndex == -1) {
	                Log.d(TAG, "Trying to open camera with old open()");
	                try {
	                    mCamera = Camera.open();
	                }
	                catch (Exception e){
	                    Log.e(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
	                }

	                if(mCamera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
	                    boolean connected = false;
	                    for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
	                        Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(camIdx) + ")");
	                        try {
	                            mCamera = Camera.open(camIdx);
	                            connected = true;
	                        } catch (RuntimeException e) {
	                            Log.e(TAG, "Camera #" + camIdx + "failed to open: " + e.getLocalizedMessage());
	                        }
	                        if (connected) break;
	                    }
	                }
	            } else {
	                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
	                    Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(mCameraIndex) + ")");
	                    try {
	                        mCamera = Camera.open(mCameraIndex);
	                    } catch (RuntimeException e) {
	                        Log.e(TAG, "Camera #" + mCameraIndex + "failed to open: " + e.getLocalizedMessage());
	                    }
	                }
	            }

	            if (mCamera == null)
	                return false;

	            /* Now set camera parameters */
	            try {
	                Camera.Parameters params = mCamera.getParameters();	                
	                Log.d(TAG, "getSupportedPreviewSizes()");
	                List<android.hardware.Camera.Size> sizes = params.getSupportedPreviewSizes();

	                if (sizes != null) {
	                    /* Select the size that fits surface considering maximum size allowed */
	                    Size frameSize = calculateCameraFrameSize(sizes, new JavaCameraSizeAccessor(), width, height);

	                    params.setPreviewFormat(ImageFormat.NV21);
	                    Log.d(TAG, "Set preview size to " + Integer.valueOf((int)frameSize.width) + "x" + Integer.valueOf((int)frameSize.height));
	                    params.setPreviewSize((int)frameSize.width, (int)frameSize.height);

	                    List<String> FocusModes = params.getSupportedFocusModes();
	                    if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
	                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	                    }

	                    // Save min, max zoom capabilities
	                    iMinZoom = 0; // Declared as default by Android camera
	                    iMaxZoom = params.getMaxZoom();

	                    // Set max zoom
	                    //params.setZoom(params.getMaxZoom());
	                    
	                    mCamera.setParameters(params);
	                    params = mCamera.getParameters();

	                    mFrameWidth = params.getPreviewSize().width;
	                    mFrameHeight = params.getPreviewSize().height;

	                    if (mFpsMeter != null) {
	                        mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
	                    }

	                    int size = mFrameWidth * mFrameHeight;
	                    size  = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
	                    mBuffer = new byte[size];

	                    mCamera.addCallbackBuffer(mBuffer);
	                    mCamera.setPreviewCallbackWithBuffer(this);

	                    mBaseMat = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);

	                    mFrameChain = new Mat[2];
	                    mFrameChain[0] = new Mat();
	                    mFrameChain[1] = new Mat();

	                    AllocateCache();

	                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	                        mSurfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
	                        mCamera.setPreviewTexture(mSurfaceTexture);
	                    } else
	                       mCamera.setPreviewDisplay(null);

	        	        // Update camera parameters
	                    this.pParams = params;
	                    
	                    /* Finally we are ready to start the preview */
	                    Log.d(TAG, "startPreview");
	                    mCamera.startPreview();
						focusCamera();
	                }
	                else
	                    result = false;
	            } catch (Exception e) {
	                result = false;
	                pParams = null;
	                e.printStackTrace();
	            }
	        }


	        return result;
	    }

	    protected void releaseCamera() {
	        synchronized (this) {
	            if (mCamera != null) {
	                mCamera.stopPreview();
	                mCamera.release();
	            }
	            mCamera = null;
	            if (mBaseMat != null)
	                mBaseMat.release();
	            if (mFrameChain != null) {
	                mFrameChain[0].release();
	                mFrameChain[1].release();
	            }
	        }
	    }

	    @Override
	    protected boolean connectCamera(int width, int height) {

	        /* 1. We need to instantiate camera
	         * 2. We need to start thread which will be getting frames
	         */
	        /* First step - initialize camera connection */
	        Log.d(TAG, "Connecting to camera");
	        if (!initializeCamera(getWidth(), getHeight()))
	            return false;

	        /* now we can start update thread */
	        Log.d(TAG, "Starting processing thread");
	        mStopThread = false;
	        mThread = new Thread(new CameraWorker());
	        mThread.start();

	        return true;
	    }

	    protected void disconnectCamera() {
	        /* 1. We need to stop thread which updating the frames
	         * 2. Stop camera and release it
	         */
	        Log.d(TAG, "Disconnecting from camera");
	        try {
	            mStopThread = true;
	            Log.d(TAG, "Notify thread");
	            synchronized (this) {
	                this.notify();
	            }
	            Log.d(TAG, "Waiting for thread");
	            if (mThread != null)
	                mThread.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } finally {
	            mThread =  null;
	        }

	        /* Now release camera */
	        releaseCamera();
	    }

	    @TargetApi(Build.VERSION_CODES.FROYO)
	    public void onPreviewFrame(byte[] frame, Camera arg1) {
	        //Log.i(TAG, "Preview Frame received. Need to create MAT and deliver it to clients");
	        //Log.i(TAG, "Frame size  is " + frame.length);
	        synchronized (this)
	        {
	            mBaseMat.put(0, 0, frame);
	            this.notify();
	        }
	        if (mCamera != null)
	            mCamera.addCallbackBuffer(mBuffer);
	    }

	    private class CameraWorker implements Runnable {

	        public void run() {
	            do {
	                synchronized (ModifiedJavaCameraView.this) {
	                    try {
	                    	ModifiedJavaCameraView.this.wait();
	                    } catch (InterruptedException e) {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
	                    }
	                }

	                if (!mStopThread) {
	                    switch (mPreviewFormat) {
	                        case Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA:
	                            Imgproc.cvtColor(mBaseMat, mFrameChain[mChainIdx], Imgproc.COLOR_YUV2RGBA_NV21, 4);
	                        break;
	                        case Highgui.CV_CAP_ANDROID_GREY_FRAME:
	                            mFrameChain[mChainIdx] = mBaseMat.submat(0, mFrameHeight, 0, mFrameWidth);
	                        break;
	                        default:
	                            Log.e(TAG, "Invalid frame format! Only RGBA and Gray Scale are supported!");
	                    };
	                    if (!mFrameChain[mChainIdx].empty())
	                        deliverAndDrawFrame(mFrameChain[mChainIdx]);
	                    mChainIdx = 1 - mChainIdx;
	                }
	            } while (!mStopThread);
	            Log.d(TAG, "Finish processing thread");
	        }
	    }
	}
