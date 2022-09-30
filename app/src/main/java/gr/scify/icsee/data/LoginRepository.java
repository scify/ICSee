package gr.scify.icsee.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import gr.scify.icsee.ICSeeStartActivity;
import gr.scify.icsee.R;
import gr.scify.icsee.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    public static final String BASE_URL = "https://kubernetes.pasiphae.eu/shapes/asapa/auth/";
    protected String action = "login";

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    // private constructor : singleton access
    private LoginRepository() {

    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public String getStoredAuthToken(Context context) {
        SharedPreferences sharedPref = context.
                getSharedPreferences(context.getString(R.string.global_preferences_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString("shapes_auth_token", null);
    }

    public void storeToken(Context context, String token) {
        SharedPreferences sharedPref = context.
                getSharedPreferences(context.getString(R.string.global_preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("shapes_auth_token", token);
        editor.apply();
    }

    public void deleteStoredUser(Context context) {
        SharedPreferences sharedPref = context.
                getSharedPreferences(context.getString(R.string.global_preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("shapes_auth_token", null);
        editor.putString("shapes_auth_email", null);
        editor.apply();
    }

    public void setStoredUser(Context context, LoggedInUser user) {
        SharedPreferences sharedPref = context.
                getSharedPreferences(context.getString(R.string.global_preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        storeToken(context, user.getAuthToken());
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(user.getEmail().getBytes());
            String stringHash = new String(messageDigest.digest());
            editor.putString("shapes_auth_email", stringHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    public void checkToken(String token, StringVolleyCallback callback) {
        StringRequest postRequest = new StringRequest(Request.Method.GET, BASE_URL + "token/verify",
                callback::onSuccess,
                callback::onError) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("X-Pasiphae-Auth", token);
                    put("X-Shapes-Key", "7Msbb3w^SjVG%j"); //API_HEADER_VALUE is provided by shapes
                    put("Content-Type", "application/json; charset=utf-8");
                }};
            }
        };
        ICSeeStartActivity.queue.add(postRequest);

    }

    public void login(String username, String password, VolleyCallback callback) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("email", username);
            jsonObject.put("password", password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL + action, jsonObject,
                response -> {
                    // response
                    try {
                        JSONArray items = response.getJSONArray("items");
                        JSONObject tokenObj = items.getJSONObject(0);
                        if (!tokenObj.has("token"))
                            return;
                        String authToken = tokenObj.getString("token");
                        Log.d("Response TOKEN", authToken);
                        LoggedInUser user =
                                new LoggedInUser(
                                        username,
                                        authToken);
                        callback.onSuccess(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError(new VolleyError(e.getMessage()));
                    }
                },
                error -> {
                    // error
                    Log.d("ERROR.RESPONSE", String.valueOf(error));
                    callback.onError(error);
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }

            @Override
            public byte[] getBody() {
                return jsonObject.toString().getBytes(Charset.forName("UTF-8"));
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>() {{
                    put("X-Shapes-Key", "7Msbb3w^SjVG%j"); //API_HEADER_VALUE is provided by shapes
                    put("Content-Type", "application/json; charset=utf-8");
                }};
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError error) {
                if (error.networkResponse == null || error.networkResponse.data == null)
                    return super.parseNetworkError(error);

                String body = new String(error.networkResponse.data, Charset.forName("UTF-8"));
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    // if login failed
                    if (jsonObject.getInt("code") == 400) {
                        if (action.equals("login")) {
                            System.out.println("login failed. Trying to signup...");
                            action = "register";
                            login(username, password, callback);
                        } else {
                            System.out.println("Signup also failed!" + jsonObject);
                            action = "login";
                            assert error.networkResponse.headers != null;
                            error.networkResponse.headers.put("SHOULD_SHOW_ERROR", String.valueOf(true));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return super.parseNetworkError(error);
                }
                return super.parseNetworkError(error);
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.data == null)
                    return super.parseNetworkResponse(response);
                // if on signup, we also need to login in order to get the
                // auth token
                if (action.equals("register")) {
                    action = "login";
                    login(username, password, callback);
                }
                return super.parseNetworkResponse(response);
            }
        };
        ICSeeStartActivity.queue.add(postRequest);
    }
}