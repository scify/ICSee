package gr.scify.icsee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageViewerActivity extends LocalizedActivity {
    protected String TAG = ImageViewerActivity.class.getCanonicalName();
    TouchImageView img;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        View view = View.inflate(mContext, R.layout.activity_image_viewer, null);
        view.setOnLongClickListener(arg0 -> {
            finish();
            return true;
        });
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        ICSeeTutorial.playAdjustZoom(mContext);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ICSeeTutorial.stopSound();
    }
}
