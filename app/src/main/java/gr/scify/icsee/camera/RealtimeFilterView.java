package gr.scify.icsee.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import gr.scify.icsee.ICSeeRealtimeActivity;
import gr.scify.icsee.camera.ModifiedCameraBridgeViewBase.CvCameraViewListener;
import gr.scify.icsee.filters.IMatFilter;

public class RealtimeFilterView extends ModifiedJavaCameraView implements CvCameraViewListener { //RealtimeViewBase {
    protected static final String TAG = RealtimeFilterView.class.getName();

    private Mat mRgba = new Mat();
    private Mat mScaledRgba; // Use

    public LinkedList<IMatFilter> lFilters;
    public NavigableSet<IMatFilter> nsCurFilters;
    protected ListIterator<IMatFilter> liCurFilter;
    protected LinkedList<NavigableSet<IMatFilter>> lPreviousSettings;

    protected boolean bProcessing = false;
    protected boolean bPaused;
    protected int processNextNFrames = 0;
    private static final String PREFS_FILE = "gr.scify.icsee.preferences";
    private static final String KEY_FILTER = "key_filter";
    private SharedPreferences.Editor mEditor;

    public RealtimeFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        lFilters = new LinkedList<>();
        lPreviousSettings = new LinkedList<>();
    }

    public void resumeCamera() {
        bPaused = false;
    }

    public void getPhoto(ShutterCallback sc, PictureCallback pcRaw, PictureCallback pcJpg, int qualitySteps) {
        try {
            int zoom = 0;
            Camera.Parameters parameters = mCamera.getParameters();
            int maxZoom = parameters.getMaxZoom();
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
            int pos = (supportedSizes.size() / 2 + 1);
            //for devices with only a few resolutions, we want the best resolution
            if (supportedSizes.size() >= 5) {
                parameters.setPictureSize(supportedSizes.get(pos).width, supportedSizes.get(pos).height);
            } else {
                parameters.setPictureSize(supportedSizes.get(0).width, supportedSizes.get(0).height);
            }

            if (parameters.isZoomSupported()) {
                if (zoom < maxZoom) {
                    parameters.setZoom(zoom);
                    mCamera.setParameters(parameters);
                }
            }
            if (mCamera != null) {
                mCamera.takePicture(sc, pcRaw, pcJpg);
            } else throw new Exception("Could not find camera... Null returned");
        } catch (Exception e) {
            Log.e(TAG, "Camera is not available!! (in use or does not exist): " + e.getLocalizedMessage() + " ," + e.getCause());
        }
    }

    public void process(int iMoreFrames) {
        processNextNFrames = iMoreFrames;
    }

    public void appendFilter(IMatFilter ibNext) {
        lFilters.add(ibNext);
        // Here we recompute the powerset of filters
        // TODO: Check if we should remove
        initFilterSubsets();
    }

    // Creates all combinations of filters
    public void initFilterSubsets() {
        liCurFilter = lFilters.listIterator();
        nsCurFilters = new TreeSet<IMatFilter>(new Comparator<IMatFilter>() {
            @Override
            public int compare(IMatFilter lhs, IMatFilter rhs) {
                return lFilters.indexOf(lhs) - lFilters.indexOf(rhs);
            }
        });
        // Clear previous lists
        lPreviousSettings.clear();
    }

    /**
     * Creates a string representation of a list of IBitmapFilters
     *
     * @param lCurFilters
     * @return
     */
    protected String filterListToString(NavigableSet<IMatFilter> lCurFilters) {
        StringBuilder sb = new StringBuilder();
        for (IMatFilter bfCur : lCurFilters)
            sb.append(bfCur.toString()).append(",");
        return sb.toString();
    }

    // Activates previous combination of filters
    public String previousFilterSubset() {
        synchronized (nsCurFilters) {
            // If no filters added, ignore.
            if (lFilters.size() == 0)
                return null;

            if (lPreviousSettings.isEmpty()) {
                return null;
            }

            nsCurFilters.clear();
            nsCurFilters = lPreviousSettings.pollLast();
            if (liCurFilter.hasPrevious())
                liCurFilter.previous();
        }

        return filterListToString(nsCurFilters);
    }

    public String curFilterSubset() {
        return filterListToString(nsCurFilters);
    }

    // Activates next combination of filter
    public String nextFilterSubset() {
        synchronized (nsCurFilters) {
            // If no filters added, ignore.
            if (lFilters.size() == 0)
                return null;

            // Keep previous settings
            if (nsCurFilters.size() > 0)
                lPreviousSettings.add(new TreeSet<>(nsCurFilters));

            // If no more filters exist after iterator
            if (!liCurFilter.hasNext()) {
                // We reset the filter iterator
                liCurFilter = lFilters.listIterator();
            } else {
                // If some filters are already used
                if (nsCurFilters.size() > 0)
                    // We remove last filter to replace it without increasing the number of filters
                    nsCurFilters.remove(nsCurFilters.last());
            }

            // Avoid repetition
            boolean bFoundNew = false;
            IMatFilter ibNext = null;
            // While there are more filters
            while (liCurFilter.hasNext()) {
                // Get the next candidate
                ibNext = liCurFilter.next();
                // If the candidate is NOT contained in the current filter set
                if (!nsCurFilters.contains(ibNext)) {
                    // We are done
                    bFoundNew = true;
                    break;
                }
            }

            // If we found a new filter to add
            if (bFoundNew)
                // Append the new filter
                nsCurFilters.add(ibNext);
            else
                return null;
        }

        return filterListToString(nsCurFilters);
    }

    static private String sFilter;

    public void saveCurrentFilterSet() {
        sFilter = curFilterSubset();
        mEditor.putString(KEY_FILTER, sFilter);
        mEditor.apply();
    }

    public void restoreCurrentFilterSet() {
        SharedPreferences mSharedPreferences = getContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        //second parameter is default value
        String sFilterName = mSharedPreferences.getString(KEY_FILTER, "");
        if (sFilterName == null)
            return; // We have not saved anything, so get back home
        ICSeeRealtimeActivity.logFilter(sFilterName);
        Log.i(TAG, "current filter (restore): " + sFilter);
        if (sFilter != null) {
            return;
        }
        String sCandidateFilterName = "";
        int iPrvFilterSetSize = -1;
        // While we have not set the saved filter as current
        //Log.i(TAG, "nsCurFilters.size(): " + nsCurFilters.size());
        // while we have not returned to less filter (i.e. restarted searching)
        while (iPrvFilterSetSize <= nsCurFilters.size()) {
            // Update current filter name
            sCandidateFilterName = curFilterSubset();
            // Update last count of filter set size
            iPrvFilterSetSize = nsCurFilters.size();
            // Get next filter or exit
            if (!sCandidateFilterName.equals(sFilterName))
                nextFilterSubset();
            else
                return;
        }

        // If we reached this point and still the name is not equal
        // we did not find the filter
        Log.i(TAG, "we did not find the filter");
        nextFilterSubset(); // Reset to first filter
    }


    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mScaledRgba = null;
    }

    public void onCameraViewStopped() {
        if (mRgba != null)
            mRgba.release();
        mRgba = null;

        if (mScaledRgba != null)
            mScaledRgba.release();
        mScaledRgba = null;
    }

    public void applyCurrentFilters(Mat mToUse) {
        // For every filter
        synchronized (mToUse) {
            synchronized (nsCurFilters) {
                for (IMatFilter bfCur : nsCurFilters) {
                    bfCur.setMat(mToUse);
                    try {
                        bfCur.applyfilter();
                        mToUse = bfCur.getMat();
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to add filter:" + bfCur.toString());
                        return;
                    }

                }
            }
        }

    }

    public Mat onCameraFrame(Mat inputFrame) {

        if (bPaused)
            if (processNextNFrames == 0)
                return null;
            else
                // Decrease if over zero
                processNextNFrames -= (processNextNFrames > 0) ? 1 : 0;


        // Copy to process
        inputFrame.copyTo(mRgba);
        // TODO: Check why copy fails
        // If the image matrix is empty
        if (mRgba.empty()) {
            // Ignore
            return null;
        }

        // Init mScaled Rgba
        if (mScaledRgba == null) {
            // Using the full possible size of the frame
            mScaledRgba = new Mat(new Size(this.getWidth(), this.getHeight()), mRgba.type());
        }

        if (bProcessing)
            return mRgba;

        bProcessing = true;

        // Apply all filters
        applyCurrentFilters(mRgba);
        // Apply resize as needed
        Imgproc.resize(mRgba, mScaledRgba, mScaledRgba.size(), 0, 0, Imgproc.INTER_LINEAR);

        bProcessing = false;

        //return mRgba;
        return mScaledRgba;
    }

    @Override
    protected void AllocateCache() {
        mCacheBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        mRgba = new Mat();
    }

}
