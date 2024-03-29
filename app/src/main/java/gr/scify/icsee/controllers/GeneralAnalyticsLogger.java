package gr.scify.icsee.controllers;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import gr.scify.icsee.ICSeeApplication;
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
            jsonObject.put("payload", bundleToJSON(additionalPayload));
            jsonObject.put("token", LoginRepository.getInstance().getStoredAuthToken(c));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // error
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, "https://memoristudio.scify.org/api/analytics/store/icsee", jsonObject,
                response -> {
                },
                Throwable::printStackTrace
        );
        ICSeeApplication.queue.add(postRequest);
    }

    protected JSONObject bundleToJSON(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, bundle.get(key));
            } catch (JSONException e) {
                //Handle exception here
            }
        }
        return json;
    }
}
