package gr.scify.icsee.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.opencv.android.FpsMeter;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.List;

/**
 * This is a basic class, implementing the interaction with Camera and OpenCV library.
 * The main responsibility of it - is to control when camera can be enabled, process the frame,
 * call external listener to make any adjustments to the frame and then draw the resulting
 * frame to the screen.
 * The clients shall implement CvCameraViewListener.
 */
public abstract class ModifiedCameraBridgeViewBase extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraBridge";
    private static final int MAX_UNSPECIFIED = -1;
    private static final int STOPPED = 0;
    private static final int STARTED = 1;

    private int mState = STOPPED;
    private CvCameraViewListener mListener;
    private boolean mSurfaceExist;
    private final Object mSyncObject = new Object();

    protected Bitmap mCacheBitmap; // Moved to protected to allow resizing
    protected int mFrameWidth;
    protected int mFrameHeight;
    protected int mMaxHeight;
    protected int mMaxWidth;
    protected int mCameraIndex;
    protected boolean mEnabled;
    protected FpsMeter mFpsMeter = null;

    public ModifiedCameraBridgeViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);

        int count = attrs.getAttributeCount();
        Log.d(TAG, "Attr count: " + count);

        mCameraIndex = gr.scify.icsee.camera.Utils.findFrontFacingCamera();
        getHolder().addCallback(this);
        mMaxWidth = MAX_UNSPECIFIED;
        mMaxHeight = MAX_UNSPECIFIED;
    }

    public interface CvCameraViewListener {
        /**
         * This method is invoked when camera preview has started. After this method is invoked
         * the frames will start to be delivered to client via the onCameraFrame() callback.
         * @param width -  the width of the frames that will be delivered
         * @param height - the height of the frames that will be delivered
         */
        void onCameraViewStarted(int width, int height);

        /**
         * This method is invoked when camera preview has been stopped for some reason.
         * No frames will be delivered via onCameraFrame() callback after this method is called.
         */
        void onCameraViewStopped();

        /**
         * This method is invoked when delivery of the frame needs to be done.
         * The returned values - is a modified frame which needs to be displayed on the screen.
         * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
         */
        Mat onCameraFrame(Mat inputFrame);

    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "call surfaceChanged event");
        synchronized(mSyncObject) {
            if (mSurfaceExist) {
                /* Pretend that old surface has been destroyed */
                mSurfaceExist = false;
                checkCurrentState();
                /* Now use new surface. Say we have it now */
            }
            mSurfaceExist = true;
            checkCurrentState();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        /* Do nothing. Wait until surfaceChanged delivered */
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized(mSyncObject) {
            mSurfaceExist = false;
            checkCurrentState();
        }
    }

    /**
     * This method is provided for clients, so they can enable the camera connection.
     * The actual onCameraViewStarted callback will be delivered only after both this method is called and surface is available
     */
    public void enableView() {
        synchronized(mSyncObject) {
            mEnabled = true;
            checkCurrentState();
        }
    }

    /**
     * This method is provided for clients, so they can disable camera connection and stop
     * the delivery of frames even though the surface view itself is not destroyed and still stays on the scren
     */
    public void disableView() {
        synchronized(mSyncObject) {
            mEnabled = false;
            checkCurrentState();
        }
    }

    /**
     *
     * @param listener
     */

    public void setCvCameraViewListener(CvCameraViewListener listener) {
        mListener = listener;
    }

    /**
     * Called when mSyncObject lock is held
     */
    private void checkCurrentState() {
        int targetState;

        if (mEnabled && mSurfaceExist && getVisibility() == VISIBLE) {
            targetState = STARTED;
        } else {
            targetState = STOPPED;
        }

        if (targetState != mState) {
            /* The state change detected. Need to exit the current state and enter target state */
            processExitState(mState);
            mState = targetState;
            processEnterState(mState);
        }
    }

    private void processEnterState(int state) {
        switch(state) {
        case STARTED:
            onEnterStartedState();
            if (mListener != null) {
                mListener.onCameraViewStarted(mFrameWidth, mFrameHeight);
            }
            break;
        case STOPPED:
            onEnterStoppedState();
            if (mListener != null) {
                mListener.onCameraViewStopped();
            }
            break;
        };
    }

    private void processExitState(int state) {
        switch(state) {
        case STARTED:
            onExitStartedState();
            break;
        case STOPPED:
            onExitStoppedState();
            break;
        };
    }

    private void onEnterStoppedState() {
        /* nothing to do */
    }

    private void onExitStoppedState() {
        /* nothing to do */
    }

    // NOTE: The order of bitmap constructor and camera connection is important for android 4.1.x
    // Bitmap must be constructed before surface
    private void onEnterStartedState() {
        /* Connect camera. The division with 2 is to improve the performance */
        if (!connectCamera(getWidth() / 2, getHeight() / 2)) {
            AlertDialog ad = new AlertDialog.Builder(getContext()).create();
            ad.setCancelable(false); // This blocks the 'BACK' button
            ad.setMessage("It seems that you device does not support camera (or it is locked). Application will be closed.");
            ad.setButton(DialogInterface.BUTTON_NEUTRAL,  "OK", (dialog, which) -> {
                dialog.dismiss();
                ((Activity) getContext()).finish();
            });
            ad.show();

        }
    }

    private void onExitStartedState() {
        disconnectCamera();
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
        }
    }

    /**
     * This method shall be called by the subclasses when they have valid
     * object and want it to be delivered to external client (via callback) and
     * then displayed on the screen.
     * @param frame - the current frame to be delivered
     */
    protected void deliverAndDrawFrame(Mat frame) {
        Mat modified;

        if (mListener != null) {
            modified = mListener.onCameraFrame(frame);
        } else {
            modified = frame;
        }

        boolean bmpValid = true;
        if (modified != null) {
            try {
                Utils.matToBitmap(modified, mCacheBitmap);
            } catch(Exception e) {
                Log.e(TAG, "Mat type: " + modified);
                Log.e(TAG, "Bitmap type: " + mCacheBitmap.getWidth() + "*" + mCacheBitmap.getHeight());
                Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
                bmpValid = false;
            }
        }

        if (bmpValid && mCacheBitmap != null) {
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                canvas.drawBitmap(mCacheBitmap, (canvas.getWidth() - mCacheBitmap.getWidth()) / 2, (canvas.getHeight() - mCacheBitmap.getHeight()) / 2, null);
                if (mFpsMeter != null) {
                    mFpsMeter.measure();
                    mFpsMeter.draw(canvas, 20, 30);
                }
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * This method is invoked shall perform concrete operation to initialize the camera.
     * CONTRACT: as a result of this method variables mFrameWidth and mFrameHeight MUST be
     * initialized with the size of the Camera frames that will be delivered to external processor.
     * @param width - the width of this SurfaceView
     * @param height - the height of this SurfaceView
     */
    protected abstract boolean connectCamera(int width, int height);

    /**
     * Disconnects and release the particular camera object being connected to this surface view.
     * Called when syncObject lock is held
     */
    protected abstract void disconnectCamera();

    // NOTE: On Android 4.1.x the function must be called before SurfaceTextre constructor!
    protected void AllocateCache()
    {
        mCacheBitmap = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);
    }

    public interface ListItemAccessor {
        int getWidth(Object obj);
        int getHeight(Object obj);
    };

    /**
     * This helper method can be called by subclasses to select camera preview size.
     * It goes over the list of the supported preview sizes and selects the maximum one which
     * fits both values set via setMaxFrameSize() and surface frame allocated for this view
     * @param supportedSizes
     * @param surfaceWidth
     * @param surfaceHeight
     * @return
     */
    protected Size calculateCameraFrameSize(List<?> supportedSizes, ListItemAccessor accessor, int surfaceWidth, int surfaceHeight) {
        int calcWidth = 0;
        int calcHeight = 0;

        int maxAllowedWidth = (mMaxWidth != MAX_UNSPECIFIED && mMaxWidth < surfaceWidth)? mMaxWidth : surfaceWidth;
        int maxAllowedHeight = (mMaxHeight != MAX_UNSPECIFIED && mMaxHeight < surfaceHeight)? mMaxHeight : surfaceHeight;

        for (Object size : supportedSizes) {
            int width = accessor.getWidth(size);
            int height = accessor.getHeight(size);

            if (width <= maxAllowedWidth && height <= maxAllowedHeight) {
                if (width >= calcWidth && height >= calcHeight) {
                    calcWidth = (int) width;
                    calcHeight = (int) height;
                }
            }
        }

        return new Size(calcWidth, calcHeight);
    }
}
