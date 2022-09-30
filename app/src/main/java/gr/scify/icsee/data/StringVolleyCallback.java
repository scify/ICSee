package gr.scify.icsee.data;

import com.android.volley.VolleyError;

public interface StringVolleyCallback {
    void onSuccess(String response);
    void onError(VolleyError data);
}