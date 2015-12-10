package gr.scify.icsee.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import gr.scify.icsee.R;
import gr.scify.icsee.TouchImageView;

public class ImageView extends Activity {
    android.widget.ImageView mImageView;
    protected String TAG = ImageView.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "ImageView created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit2);
        TouchImageView img = new TouchImageView(this);
        //img = (gr.scify.icsee.TouchImageView) findViewById(R.id.imageView);
        //mImageView = (android.widget.ImageView) findViewById(R.id.imageView);
        Log.i(TAG, "ImageView created");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] byteArray = getIntent().getByteArrayExtra("image");
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            img.setImageBitmap(bitmap);
            img.setMaxZoom(4f);
            setContentView(img);
        }

    }

}
