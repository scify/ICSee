package gr.scify.icsee;

import gr.scify.icsee.camera.RealtimeFilterView;
import gr.scify.icsee.filters.opencv.matfilters.MatAdaptiveThresholding;
import gr.scify.icsee.filters.opencv.matfilters.MatBinarizationFilter;
import gr.scify.icsee.filters.opencv.matfilters.MatNegative;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ICSeeRealtimeActivity extends Activity implements
	OnGesturePerformedListener {
	private GestureLibrary gestureLib;
	protected int MAX_ZOOM = 5;
	private RealtimeFilterView mView = null;
	
	
	private String TAG = ICSeeRealtimeActivity.class.getCanonicalName();

    private BaseLoaderCallback  mOpenCVCallBack = new BaseLoaderCallback(this) {

		@Override
    	public void onManagerConnected(int status) {
    		switch (status) {
				case LoaderCallbackInterface.SUCCESS:
				{
					Log.i(TAG, "OpenCV loaded successfully");
					// TODO: Determine active camera
					
			        // Get camera num from prefs
//			        SharedPreferences spPrefs = ICSeeRealtimeActivity.this.getPreferences(MODE_PRIVATE);
//					int cameraNum = spPrefs.getInt("cameraNum", 1);
					if (mView == null) {
					
						// Create and set View
						mView = (RealtimeFilterView) findViewById(R.id.pbPreview);
						
						// Allow long clicks
						mView.setLongClickable(true);
						mView.appendFilter(new MatAdaptiveThresholding());
						mView.appendFilter(new MatBinarizationFilter());
						
//						mView.appendFilter(new MatHistogramEqualization());
//						mView.appendFilter(new MatSmoothFilterMedian());
//						mView.appendFilter(new MatBlurFilter());
//						mView.appendFilter(new MatEdgeDetectionCannyFilter());
						
						mView.appendFilter(new MatNegative());
//						mView.appendFilter(new MatBlueFilter());
					}
					// Asynchronous test for surface creation
					Thread tCheckForSurface = new Thread(new Runnable() {
						
						@Override
						public void run() {
							while (mView == null) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							while (mView.getVisibility() != View.VISIBLE) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}					
							}
							
							if (mView.getVisibility() == View.VISIBLE) {
						        mView.setCvCameraViewListener(mView);
					            mView.enableView();
							}				
						}
					});
					tCheckForSurface.start();
				} break;
				default:
				{
					super.onManagerConnected(status);
				} break;
			}
    	}
	};
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
	    View inflate = getLayoutInflater().inflate(R.layout.custom, null);
	    gestureOverlayView.addView(inflate);
	    gestureOverlayView.addOnGesturePerformedListener(this);
	    gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
	    if (!gestureLib.load()) {
	      finish();
	    }
		// Init Handler
		
        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mOpenCVCallBack))
        {
        	Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
		setContentView(R.layout.activity_main);
//		if (detector == null)
//			detector = new SimpleGestureFilter(ICSeeRealtimeActivity.this, ICSeeRealtimeActivity.this);
		inflate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				mView.focusCamera();
//				if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
//					mView.FlashMode();
//				}

			}
		});
		inflate.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View arg0) {
            	if(mView.camerastate()==false){
            		mView.pauseCamera();
            		
            	}else{
            		mView.resumeCamera();
            	}
            	
            	return true;                
            }
		});
		
		LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.MATCH_PARENT, 
					LayoutParams.MATCH_PARENT);
		this.addContentView(gestureOverlayView, layoutParamsControl);		
        
	}

	
	@Override
	protected void onPause() {
        if (mView != null)
            mView.disableView();
		super.onPause();
//		if (null != mView)
//			mView.releaseCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mOpenCVCallBack);
        if (mView != null)
            mView.enableView();
	}

	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent me) {
//		this.detector.onTouchEvent(me);
//		return super.dispatchTouchEvent(me);
//	}

//	@Override
//	public void onSwipe(int direction) {
//		if (mView.getCamera() == null)
//			return;
//		Camera.Parameters params = mView.mCamera.getParameters();
//		int zoom = params.getZoom();
//		switch (direction) {
//		case SimpleGestureFilter.SWIPE_RIGHT:
/**			String sTheme = mView.nextFilterSubset();
			// Process frame to show results
			mView.process(1);

			if (sTheme != null)
				Toast.makeText(ICSeeRealtimeActivity.this, "Next Theme: " + sTheme,
					Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(ICSeeRealtimeActivity.this, "No theme applicable",
						Toast.LENGTH_SHORT).show();
						*/
//			break;
//		case SimpleGestureFilter.SWIPE_LEFT:
/**			sTheme = mView.previousFilterSubset();
			// Process frame to show results
			mView.process(1);

			if (sTheme != null)
				Toast.makeText(ICSeeRealtimeActivity.this, "Previous Theme: " + sTheme,
					Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(ICSeeRealtimeActivity.this, "No theme applicable",
						Toast.LENGTH_SHORT).show();
						*/
//			break;
//		case SimpleGestureFilter.SWIPE_UP:

//			if (mView.increaseZoom())
//				showFilters();
				
//			mView.focusCamera();

			// OBSOLETE: Old zoom
//			if (mView.getZoom() < MAX_ZOOM) {
//				mView.setZoom(mView.getZoom() + 0.2);
//			}	
//			// Process frame to show results
//			mView.process(1);

			// OBSOLETE: Filter threshold change
//			// Get filters			
//			List<IMatFilter> lCurFilters = mView.getCurrentFilters();
//			// Increase parameter of the last filter only
//			if (lCurFilters.size() > 0)
//				lCurFilters.get(lCurFilters.size() - 1).increaseParameter();
//			
//			// Update user
//			showFilters();
			
//			break;
//		case SimpleGestureFilter.SWIPE_DOWN:
//			if (mView.decreaseZoom())
//				showFilters();
//			mView.focusCamera();
			
			
			// OBSOLETE: Old zoom
//			if (mView.getZoom() > 1) {
//				mView.setZoom(mView.getZoom() - 0.2);
//			}
//			// Process frame to show results
//			mView.process(1);
			
			// OBSOLETE: Filter threshold change
//			// Get filters			
//			lCurFilters = mView.getCurrentFilters();
//			// Decrease last parameter
//			if (lCurFilters.size() > 0)
//				lCurFilters.get(lCurFilters.size() - 1).decreaseParameter();
//			// Update user
//			showFilters();
			
//			break;
//		}
//	}

	public void showFilters() {
		String sTheme = mView.curFilterSubset();
		Toast.makeText(ICSeeRealtimeActivity.this, "Next Theme: " + sTheme, Toast.LENGTH_SHORT).show();		
	}
	
//	@Override
//	public void onLongPress() {
//		mView.focusCamera();
		
//		mView.pauseCamera();
//		// TODO: Get photo and process
//		mView.disableView();
//		
//		mView.getPhoto(new Camera.ShutterCallback() {
//			
//			@Override
//			public void onShutter() {
//				// Ignore
//				
//			}
//		}, new Camera.PictureCallback() {
//			
//			@Override
//			public void onPictureTaken(byte[] data, Camera camera) {
//				// Ignore
//				
//			}
//		}, new Camera.PictureCallback() {
//			
//			@Override
//			public void onPictureTaken(byte[] data, Camera camera) {
//				if (data == null) // If no data provided
//					return; // Ignore call
//				
//				// Got photo
//				// Init memory
//				Mat mPhoto = new Mat();
//				// Process
//				Bitmap bCur = BitmapFactory.decodeByteArray(data, 0, data.length);
//				Utils.bitmapToMat(bCur, mPhoto);
//				mPhoto = mView.applyCurrentFilters(mPhoto);
//				// Convert to bitmap
//				Utils.matToBitmap(mPhoto, bCur);
//				// Clear existing mat
//				mPhoto.release();
//				
//				//Convert to byte array
//				ByteArrayOutputStream stream = new ByteArrayOutputStream();
//				bCur.compress(Bitmap.CompressFormat.PNG, 100, stream);
//				byte[] byteArray = stream.toByteArray();
//
//				// Send bitmap to ImgViewerActivity
//				Intent iShowImg = new Intent(ICSeeRealtimeActivity.this, ImgViewerActivity.class);
//				iShowImg.putExtra(ImgViewerActivity.IMG_ITEM, byteArray);
//
//				// Release camera
//				camera.release();
//
//				// Start activity
//				startActivityForResult(iShowImg, 1);
//				// Release
//				bCur.recycle();
//			}
//		});
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		mView.enableView();
		mView.resumeCamera();		
	}
	
	
//	@Override
//	public void onDoubleTap() {
		// Ignore for now
//		mView.initFilterSubsets();
//	}
	
//	@Override
//	public void onSingleTapUp() {
		// Ignore 
//	}

//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		MenuInflater inflater = getMenuInflater();
//
//		inflater.inflate(R.menu.activity_main, menu);
//
//		return true;
//
//	}
//
//	// set items for the menu
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle item selection
//		switch (item.getItemId()) {
//		case R.id.menu_settings:
//			startActivity(new Intent(this, SettingsActivity.class));
//			return true;
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
	
	@SuppressWarnings("unchecked")
	protected List<List<Object>> getCombinationsBy(Object oObj, int iBySize) {
		List<List<Object>> uRes = new ArrayList<List<Object>>();

		List<Object> lList;
		// If unary, wrap in list.
		if (!(oObj instanceof List)) {
			lList = new ArrayList<Object>();
			lList.add(oObj);
		} else
			lList = (List<Object>) oObj;

		int[] indices;
		CombinationGenerator cgGen = new CombinationGenerator(lList.size(),
				iBySize);
		while (cgGen.hasMore()) {
			List<Object> cComb = new ArrayList<Object>();
			indices = cgGen.getNext();
			for (int i = 0; i < indices.length; i++) {
				cComb.add(lList.get(indices[i]));
			}
			uRes.add(cComb);
		}
		return uRes;
	}



    
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
      ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
      String sTheme ;
      TextView sliderText = (TextView)findViewById(R.id.verticalSeekbarText);
      for (Prediction prediction : predictions) {
        if (prediction.score > 1.0) {
      //    Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
        //      .show();
          if(prediction.name.contains("right")){
        	  sTheme = mView.nextFilterSubset();
			// Process frame to show results
			mView.process(1);

			if (sTheme != null){
				sliderText.setText("Next Theme: " + sTheme);
				//Toast.makeText(ICSeeRealtimeActivity.this, "Next Theme: " + sTheme,
				//	Toast.LENGTH_SHORT).show();
			}else{
				mView.initFilterSubsets();
				sliderText.setText("No theme applicable");
				//Toast.makeText(ICSeeRealtimeActivity.this, "No theme applicable",
				//		Toast.LENGTH_SHORT).show();
			}
	        }else{
	    		sTheme = mView.previousFilterSubset();
				// Process frame to show results
				mView.process(1);

				if (sTheme != null){
					sliderText.setText("Previous Theme: " + sTheme);
					//Toast.makeText(ICSeeRealtimeActivity.this, "Previous Theme: " + sTheme,
					//	Toast.LENGTH_SHORT).show();
				}else{
					mView.initFilterSubsets();
					sliderText.setText("No theme applicable");
				//	Toast.makeText(ICSeeRealtimeActivity.this, "No theme applicable",
				//			Toast.LENGTH_SHORT).show();
				}
	        }
        }
      }
    }
}



class CombinationGenerator {

	  private int[] a;
	  private int n;
	  private int r;
	  private BigInteger numLeft;
	  private BigInteger total;

	  //------------
	  // Constructor
	  //------------

	  public CombinationGenerator (int n, int r) {
	    if (r > n) {
	      throw new IllegalArgumentException ();
	    }
	    if (n < 1) {
	      throw new IllegalArgumentException ();
	    }
	    this.n = n;
	    this.r = r;
	    a = new int[r];
	    BigInteger nFact = getFactorial (n);
	    BigInteger rFact = getFactorial (r);
	    BigInteger nminusrFact = getFactorial (n - r);
	    total = nFact.divide (rFact.multiply (nminusrFact));
	    reset ();
	  }

	  //------
	  // Reset
	  //------

	  public void reset () {
	    for (int i = 0; i < a.length; i++) {
	      a[i] = i;
	    }
	    numLeft = new BigInteger (total.toString ());
	  }

	  //------------------------------------------------
	  // Return number of combinations not yet generated
	  //------------------------------------------------

	  public BigInteger getNumLeft () {
	    return numLeft;
	  }

	  //-----------------------------
	  // Are there more combinations?
	  //-----------------------------

	  public boolean hasMore () {
	    return numLeft.compareTo (BigInteger.ZERO) == 1;
	  }

	  //------------------------------------
	  // Return total number of combinations
	  //------------------------------------

	  public BigInteger getTotal () {
	    return total;
	  }

	  //------------------
	  // Compute factorial
	  //------------------

	  private static BigInteger getFactorial (int n) {
	    BigInteger fact = BigInteger.ONE;
	    for (int i = n; i > 1; i--) {
	      fact = fact.multiply (new BigInteger (Integer.toString (i)));
	    }
	    return fact;
	  }

	  //--------------------------------------------------------
	  // Generate next combination (algorithm from Rosen p. 286)
	  //--------------------------------------------------------

	  public int[] getNext () {

	    if (numLeft.equals (total)) {
	      numLeft = numLeft.subtract (BigInteger.ONE);
	      return a;
	    }

	    int i = r - 1;
	    while (a[i] == n - r + i) {
	      i--;
	    }
	    a[i] = a[i] + 1;
	    for (int j = i + 1; j < r; j++) {
	      a[j] = a[i] + j - i;
	    }

	    numLeft = numLeft.subtract (BigInteger.ONE);
	    return a;

	  }
}
