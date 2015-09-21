package gr.scify.icsee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import gr.scify.icsee.camera.ModifiedLoaderCallback;
import gr.scify.icsee.sounds.SoundPlayer;


public class ICSeeStartActivity extends Activity {
    protected Context mContext;
    public ModifiedLoaderCallback mOpenCVCallBack;
    public ProgressDialog mDialog;
    protected String TAG = ICSeeRealtimeActivity.class.getCanonicalName();
    Button mExitButton;
    ProgressBar mProgressBar;
    File file = new File("/data/data/gr.scify.icsee/files/configTutorial.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        mContext = this;

        // Initialize sounds here so they should have loaded when the camera view starts
        SoundPlayer.initSounds(this.getApplicationContext());

        /*if(!file.exists()) {
            file = createFile("configTutorial.txt");
            Log.i(TAG, "file: " + file.exists());
            Log.i(TAG, "fileName: " + file.getName());
            writeToFile(file, "Tutorial has played");
            SoundPlayer.playSound(mContext, SoundPlayer.Stutorial);
        } else {
            Log.i(TAG, readFromFile(file.getPath()));
            //TODO: play tutorial reminder
        }*/

        mExitButton = (Button)findViewById(R.id.exitButton);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        mExitButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG, "click!");
                Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.vibrate(250);
                SoundPlayer.playSound(mContext, SoundPlayer.Stutorial);
                new AsyncProgressCheck(mDialog, mOpenCVCallBack,ICSeeStartActivity.this).execute();
                return false;
            }
        });
        mOpenCVCallBack = new ModifiedLoaderCallback(mContext, mProgressBar);

    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Trying to load OpenCV library");
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mOpenCVCallBack);

    }

    public void showErrorMessage(String sMsg, boolean bShouldClose) {
        // Hide progress bar
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(pb.INVISIBLE);

        // Set errorMessage text
        TextView tvMsg = (TextView)findViewById(R.id.errorMessage);
        tvMsg.setText(sMsg);

        // Show errorMessage text
        tvMsg.setVisibility(tvMsg.VISIBLE);

        // Show button
        Button bExitBtn = (Button)findViewById(R.id.exitButton);
        bExitBtn.setVisibility(bExitBtn.VISIBLE);

        // If bShouldClose
        if (bShouldClose) {
            bExitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // exit activity
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        Log.i(TAG, "touch: " + event.getActionMasked());

        return super.onTouchEvent(event);
    }


    private void writeToFile(File file, String data) {
        try {

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(file.getName(), Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.i(TAG, "File: " + file.getPath());
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private File createFile(String fileName) {
        File file = new File(mContext.getFilesDir() +File.separator + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    private String readFromFile(String file) {

        String ret = "";

        try {
            FileInputStream inputStream = new FileInputStream(new File(file));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return ret;
    }
}

