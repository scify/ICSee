package gr.scify.icsee;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class ICSeeStartActivity extends Activity{
	
	private String TAG = ICSeeStartActivity.class.getCanonicalName();
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    Intent intent = new Intent(ICSeeStartActivity.this, ICSeeRealtimeActivity.class);
				    startActivity(intent);
				    finish();
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
//	        Button click = new Button(this);
//	        click.setText("clickme");
//	        click.setLayoutParams(
//	        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//	        							  LinearLayout.LayoutParams.MATCH_PARENT,1.0f));
	        LinearLayout layout = new LinearLayout(this);
	        layout.setLayoutParams(
	        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
	        							  LinearLayout.LayoutParams.MATCH_PARENT));
	        
//	        layout.addView(click);
	        setContentView(layout);
	        
//	        click.setOnClickListener( new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					Intent intent = new Intent(ICSeeStartActivity.this, ICSeeRealtimeActivity.class);
//				    startActivity(intent);
					
//				}
//	        });
	        
	 }
	 @Override
	    public void onResume()
	    {
	        super.onResume();
	        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);
	    }
}
