package gr.scify.icsee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.opencv.android.LoaderCallbackInterface;

import gr.scify.icsee.camera.ModifiedLoaderCallback;
import gr.scify.icsee.camera.RealtimeFilterView;

/**
 * Created by scifi on 4/8/2014.
 */




public class ProgressThread extends AsyncTask<Void,Void,Void> {

    Context context;
    ProgressDialog mDialog;
    ModifiedLoaderCallback mOpenCVCallBack;

    ProgressThread (ProgressDialog pd, ModifiedLoaderCallback aa) {
        mDialog = pd;
        mOpenCVCallBack = aa;

    }


    @Override
    protected Void doInBackground(Void... params) {
    switch (mOpenCVCallBack.processStatus){
            case LoaderCallbackInterface.SUCCESS:
            {
                context.startActivity(new Intent(context, ICSeeRealtimeActivity.class));
            }break;
            default:
            {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } break;
        }
        return null;
    }

    protected void onPostExecute(){
        super.onPostExecute(null);
        mDialog.dismiss();


    }





}
