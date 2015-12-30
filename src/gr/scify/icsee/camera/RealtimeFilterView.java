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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    protected double CurrentZoom = 1.0;

	public LinkedList<IMatFilter> lFilters;
	public NavigableSet<IMatFilter> nsCurFilters;
	protected ListIterator<IMatFilter> liCurFilter;
	protected LinkedList<NavigableSet<IMatFilter>> lPreviousSettings;
	
	protected boolean bProcessing = false;
	protected boolean bPaused;
	protected int processNextNFrames = 0;
    private static final String PREFS_FILE = "gr.scify.icsee.preferences";
    private static final String KEY_FILTER = "key_filter";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

	public RealtimeFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
        lFilters = new LinkedList<IMatFilter>();
        lPreviousSettings = new LinkedList<NavigableSet<IMatFilter>>();
	}
	
    public RealtimeFilterView(Context context, int cameraId) {
        super(context, cameraId);
        lFilters = new LinkedList<IMatFilter>();
        lPreviousSettings = new LinkedList<NavigableSet<IMatFilter>>();
    }

    public boolean pauseCamera() {
    	bPaused = true;
    	return bPaused;
    }
    
    public boolean resumeCamera() {
    	bPaused = false;
    	return bPaused;
    }
    
    public boolean camerastate(){
		return bPaused;
    }
    
    public void getPhoto(ShutterCallback sc, PictureCallback pcRaw, PictureCallback pcJpg, int qualitySteps) {
        try {
            //mCamera = Camera.open();
			int zoom = 0;
			Camera.Parameters parameters = mCamera.getParameters();
			int maxZoom = parameters.getMaxZoom();
			//Log.i(TAG, "currentZoom: " + parameters.getZoom());
			Log.i(TAG, "maxZoom: " + maxZoom);
			Camera.Parameters params = mCamera.getParameters();
			List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
            for(int i=0; i< supportedSizes.size() ;i++) {
                int height = supportedSizes.get(i).height;
                int width = supportedSizes.get(i).width;
                //Log.i(TAG, "height: " + height + " width: " + width);
            }
            int pos = (supportedSizes.size() / 2 + 1);
            parameters.setPictureSize(supportedSizes.get(pos).width, supportedSizes.get(pos).height);
			if (parameters.isZoomSupported()) {
				if (zoom >=0 && zoom < maxZoom) {
					parameters.setZoom(zoom);
					mCamera.setParameters(parameters);

				} else {
					// zoom parameter is incorrect
				}
			}
            if (mCamera != null) {
            	mCamera.takePicture(sc , pcRaw, pcJpg);
            }
            else throw new Exception("Cound not find camera... Null returned");
        }
        catch (Exception e){
            Log.e(TAG, "Camera is not available!! (in use or does not exist): " + e.getLocalizedMessage() + " ," + e.getCause());
        }
    }
    
    public void process(int iMoreFrames) {
    	processNextNFrames = iMoreFrames;
    }
    
	public void setZoom(double currentZoom) {
		CurrentZoom = currentZoom;
	}
	
	public double getZoom() {
		return CurrentZoom;
	}
	
	public List<IMatFilter> getAttachedFilters() {
		return lFilters;
	}
    
	public List<IMatFilter> getCurrentFilters() {
		return new ArrayList<IMatFilter>(nsCurFilters);
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
     * @param lCurFilters
     * @return
     */
    protected String filterListToString(NavigableSet<IMatFilter> lCurFilters) {
    	StringBuffer sb = new StringBuffer();
    	for (IMatFilter bfCur : lCurFilters)
    		sb.append(bfCur.toString()).append(",");
    	return sb.toString();
    }

    public NavigableSet<IMatFilter> filterListToString(String sFilterNames) {
    	// Init filter string representation to filter.
    	HashMap<String, IMatFilter> mFiltersByName = new HashMap<String, IMatFilter>();
    	// For all registered filters
    	for (IMatFilter bfCur : lFilters) {
    		// Add their entry to the map
    		mFiltersByName.put(bfCur.toString(), bfCur);
    	}
    	
    	TreeSet<IMatFilter> nsRes = new TreeSet<IMatFilter>(new Comparator<IMatFilter>() {
    		@Override
    		public int compare(IMatFilter lhs, IMatFilter rhs) {
    			return lFilters.indexOf(lhs) - lFilters.indexOf(rhs);
    		}    		
		});
    	
    	// For each filtername in the filter string
    	for (String sFilterName : sFilterNames.split("[,]")) {
    		// If it exists in our current allowed filter set
    		if (mFiltersByName.containsKey(sFilterName))
    			nsRes.add(mFiltersByName.get(sFilterName));
    	}
    	
    	return nsRes;
    }
    // Activates previous combination of filters
    public String previousFilterSubset() {
    	synchronized (nsCurFilters) {
			Log.d("fil", "lFilters: " + lFilters);
			Log.d("fil", "lPreviousSettings: " + lPreviousSettings);
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

    /**
     * This method selects the next sub filter, but without generating
     * permutations of filters. Only the basic, appended list.
     * @return
     */
    public String nextSingleFilter() {
        if (!liCurFilter.hasNext()) {
            // We reset the filter iterator
            liCurFilter = lFilters.listIterator();
        }
        IMatFilter ibNext = liCurFilter.next();
        nsCurFilters.clear();
        nsCurFilters.add(ibNext);
        return filterListToString(nsCurFilters);
    }

    // Activates next combination of filter
    public String nextFilterSubset() {
		//Log.d("fil", "nsCurFilters: " + nsCurFilters);
		//Log.d("fil", "lFilters: " + lFilters);
    	synchronized (nsCurFilters) {
    		// If no filters added, ignore.
    		if (lFilters.size() == 0)
    			return null;
    		
    		// Keep previous settings
    		if (nsCurFilters.size() > 0)
    			lPreviousSettings.add(new TreeSet<IMatFilter>(nsCurFilters));
    		
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
		// Get string representation
		sFilter  = curFilterSubset();
		// TODO: Save to a setting
		Log.i(TAG, "current filter: " + sFilter);
        mEditor.putString(KEY_FILTER, sFilter);
        mEditor.apply();
	}
	public void restoreCurrentFilterSet() {
        mSharedPreferences = getContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        //second parameter is default value
		String sFilterName = mSharedPreferences.getString(KEY_FILTER,"");
        if (sFilterName == null)
            return; // We have not saved anything, so get back home
        Log.i(TAG, "current filter (restore): " + sFilter);

		String sCandidateFilterName = "";
		int iPrvFilterSetSize = -1;
		// While we have not set the saved filter as current
        Log.i(TAG, "nsCurFilters.size(): " + nsCurFilters.size());
        // while we have not returned to less filter (i.e. restarted searching)
		while (iPrvFilterSetSize <= nsCurFilters.size()) {
			Log.i(TAG, "sCandidateFilterName: " + sCandidateFilterName);
			Log.i(TAG, "sFilter: " + sFilter);
			// Update current filter name
			sCandidateFilterName = curFilterSubset();
            // Update last count of filter set size
            iPrvFilterSetSize = nsCurFilters.size();
            // Get next filter or exit
            if(!sCandidateFilterName.equals(sFilterName))
			    nextFilterSubset();
            else
                return;
		}

		// If we reached this point and still the name is not equal
		if (!sCandidateFilterName.equals(sFilterName)) {
            // we did not find the filter
            Log.i(TAG, "we did not find the filter");
            nextFilterSubset(); // Reset to first filter
        }
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
    
    public Mat applyCurrentFilters(Mat mToUse) {
        // For every filter
        synchronized (mToUse) {
            synchronized (nsCurFilters) {
	            for (IMatFilter bfCur : nsCurFilters) {
	            	bfCur.setMat(mToUse);

	            	// Get filter
	            	bfCur.applyfilter();
	            	mToUse = bfCur.getMat();
	            }
            }
		}
    	
        return mToUse;
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
    	// Init mScaled Rgba
    	if (mScaledRgba == null)
    		// Using the full possible size of the frame
    		mScaledRgba = new Mat(new Size(this.getWidth(), this.getHeight()), mRgba.type());
    	
    	if (bProcessing)
    		return mRgba;
    	
        bProcessing = true;
        
        // For every filter
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
