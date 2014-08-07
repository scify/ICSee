package gr.scify.icsee.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.ViewDebug;

public final class Utils {

	protected static String DEBUG_TAG = Utils.class.getCanonicalName();
    protected static Context bContext;




    @ViewDebug.CapturedViewProperty
    public static Context getContext() {
        return bContext;
    }


	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}

	public static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            } else {
                cameraId = -1;
            }
            return cameraId;
        }

        if (cameraId < 0) {


            AlertDialog noBackCamera = new AlertDialog.Builder(getContext()).create();
            noBackCamera.setCancelable(false);
            noBackCamera.setMessage("Your device does not have a back camera");

            noBackCamera.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    System.exit(0);

                }
            });
            noBackCamera.show();
        }
        return cameraId;
    }
}






