package gr.scify.icsee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

import gr.scify.icsee.events.SimpleGestureFilter;
import gr.scify.icsee.events.SimpleGestureFilter.SimpleGestureListener;

public class ImgViewerActivity extends Activity implements SimpleGestureListener {
	public final static String IMG_ITEM = "photo";
	
	protected ImageView ivCur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img_viewer);
		Intent iCaller = getIntent();
		byte[] baBmp = iCaller.getByteArrayExtra(IMG_ITEM);
		Bitmap bmp = BitmapFactory.decodeByteArray(baBmp, 0, baBmp.length);
		ivCur = (ImageView)findViewById(R.id.processedImageView);
		ivCur.setImageBitmap(bmp);
		new SimpleGestureFilter(this, this);
	}

	@Override
	public void onDoubleTap() {
		finish();		
	}

	@Override
	public void onLongPress() {
		// Ignore		
	}
	
	@Override
	public void onSingleTapUp() {
		// Ignore
	}
	@Override
	public void onSwipe(int direction) {
		// Ignore
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.img_viewer, menu);
		return true;
	}

}
