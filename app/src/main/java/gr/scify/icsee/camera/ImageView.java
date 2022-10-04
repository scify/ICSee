package gr.scify.icsee.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gr.scify.icsee.ICSeeTutorial;
import gr.scify.icsee.LocaleManager;
import gr.scify.icsee.R;
import gr.scify.icsee.TouchImageView;

public class ImageView extends Activity {
    protected String TAG = ImageView.class.getCanonicalName();
    TouchImageView img;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setAppLocale(getBaseContext());
        setContentView(R.layout.activity_image_edit2);
        img = new TouchImageView(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String dir = extras.getString("dir");
            Log.i(TAG, dir);
            try {
                File f = new File(dir, "profile.png");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                img.setImageBitmap(b);
                img.setMaxZoom(4f);
                setContentView(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "playSound");
        ICSeeTutorial.playAdjustZoom(mContext);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ICSeeTutorial.stopSound();
    }
}
