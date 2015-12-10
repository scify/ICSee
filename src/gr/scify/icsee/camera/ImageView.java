package gr.scify.icsee.camera;

import android.animation.Animator;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import gr.scify.icsee.R;
import gr.scify.icsee.TouchImageView;
import gr.scify.icsee.sounds.SoundPlayer;

public class ImageView extends Activity {
    android.widget.ImageView mImageView;
    protected String TAG = ImageView.class.getCanonicalName();
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit2);
        TouchImageView img = new TouchImageView(this);
        //img = (gr.scify.icsee.TouchImageView) findViewById(R.id.imageView);
        //mImageView = (android.widget.ImageView) findViewById(R.id.imageView);
        //Log.i(TAG, "created");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] byteArray = getIntent().getByteArrayExtra("image");
            Log.i(TAG, "Byte array length: " + byteArray.length);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("image");
            /*mImageView.setImageBitmap(bitmap);
            mImageView.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);*/
            img.setImageBitmap(bitmap);
            img.setMaxZoom(4f);
            setContentView(img);
        }

    }

}
