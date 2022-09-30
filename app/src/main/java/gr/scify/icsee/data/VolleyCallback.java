package gr.scify.icsee.data;

import com.android.volley.VolleyError;

import gr.scify.icsee.data.model.LoggedInUser;

public interface VolleyCallback{
    void onSuccess(LoggedInUser user);
    void onError(VolleyError data);
}