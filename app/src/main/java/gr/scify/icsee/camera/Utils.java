package gr.scify.icsee.camera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

public final class Utils {

    private static final String DEBUG_TAG = Utils.class.getCanonicalName();

    public static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras() - 1;
        for (int i = 0; i <= numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == 0) {
                Log.d(DEBUG_TAG, "Back Camera found");
                cameraId = 0;
                break;
            }
        }
        return cameraId;
    }
}






