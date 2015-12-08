package gr.scify.icsee.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import gr.scify.icsee.R;

public class ImageView extends Activity {
    android.widget.ImageView mImageView;
    protected String TAG = ImageView.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit2);
        mImageView = (android.widget.ImageView) findViewById(R.id.imageView);
        Log.i(TAG, "created");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] byteArray = getIntent().getByteArrayExtra("image");
            Log.i(TAG, "Byte array length: " + byteArray.length);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("image");
            mImageView.setImageBitmap(bitmap);
            mImageView.setScaleType(android.widget.ImageView.ScaleType.FIT_XY);
        }

    }

}
