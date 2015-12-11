package gr.scify.icsee.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gr.scify.icsee.R;
import gr.scify.icsee.TouchImageView;

public class ImageView extends Activity {
    protected String TAG = ImageView.class.getCanonicalName();
    TouchImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "ImageView created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit2);
        img = new TouchImageView(this);
        Log.i(TAG, "ImageView created");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String dir = extras.getString("dir");
            Log.i(TAG, dir);
            try {
                File f=new File(dir, "profile.png");
                if(f.exists()) {
                    Log.i(TAG, "file exists");
                } else {
                    Log.i(TAG, "file does not exist");
                }
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                img.setImageBitmap(b);
                img.setMaxZoom(4f);
                setContentView(img);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
}
