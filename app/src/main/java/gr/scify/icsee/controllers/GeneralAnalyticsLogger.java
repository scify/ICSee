package gr.scify.icsee.controllers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import gr.scify.icsee.ICSeeStartActivity;
import gr.scify.icsee.data.LoginRepository;
import gr.scify.icsee.interfaces.IAnalyticsLogger;

public class GeneralAnalyticsLogger implements IAnalyticsLogger {

    @Override
    public void logEvent(Context c, String eventName, String eventValue, Bundle additionalPayload) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", eventName);
            jsonObject.put("source", "icsee_mobile");
            jsonObject.put("action", eventName);
            jsonObject.put("value", eventValue);
            jsonObject.put("payload", additionalPayload);
            jsonObject.put("token", LoginRepository.getInstance().getStoredAuthToken(c));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, "https://memoristudio.scify.org/api/analytics/store/icsee", jsonObject,
//                response -> {
//                    Log.d("ANALYTICS.RESPONSE", String.valueOf(response));
//                },
//                error -> {
//                    // error
//                    error.printStackTrace();
//                    Log.d("ERROR.RESPONSE", String.valueOf(error.getMessage()));
//                }
//        );
//        ICSeeStartActivity.queue.add(postRequest);
    }
}
