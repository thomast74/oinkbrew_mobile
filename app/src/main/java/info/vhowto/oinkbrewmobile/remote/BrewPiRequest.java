package info.vhowto.oinkbrewmobile.remote;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.OinkbrewApplication;
import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.BrewPi;
import info.vhowto.oinkbrewmobile.helpers.HttpsTrustManager;

public class BrewPiRequest {

    private static final String TAG = BrewPiRequest.class.getSimpleName();
    private static final String brewpisGeneral = "%s/brewpis/";
    private static final String brewpiDetail = "%s/brewpis/%s/%s/";
    private static final int MY_SOCKET_TIMEOUT_MS = 60000;

    public static void getBrewPis(final RequestArrayCallback callback) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callback.getApplicationContext());
        String apiUrl = prefs.getString("pref_api_server_url", "");
        Boolean allowAllCerts = prefs.getBoolean("pref_api_allow_all_certs", false);

        if (allowAllCerts)
            HttpsTrustManager.allowAllSSL();
        else
            HttpsTrustManager.allowOnlyValidSSL();

        if (apiUrl.isEmpty()) {
            callback.onRequestFailure(0, callback.getApplicationContext()
                    .getString(R.string.error_settings_api_url_missing));
            return;
        }

        String url = String.format(brewpisGeneral, apiUrl);

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        ArrayList<BrewPi> brewpis = new ArrayList<>();

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    BrewPi brewpi= BrewPi.fromJson(response.getJSONObject(i).toString());
                                    brewpis.add(brewpi);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }
                        }
                        callback.onRequestSuccessful(brewpis);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.getMessage(), error);

                        String errorMessage = error.getMessage();
                        if (error.networkResponse != null && error.networkResponse.data.length > 0) {
                            try {
                                errorMessage = new String(error.networkResponse.data, "UTF-8");
                                JSONObject jsonResonse = new JSONObject(errorMessage);
                                errorMessage = jsonResonse.getString("Message");
                            }
                            catch (UnsupportedEncodingException e) {
                                Log.d(TAG, e.getMessage(), e);
                            }
                            catch (JSONException e) {
                                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                            }
                        }

                        callback.onRequestFailure(
                                error.networkResponse == null ? 0 : error.networkResponse.statusCode,
                                errorMessage);
                    }
                });

        req.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, 0, 0));
        OinkbrewApplication.getInstance().addToRequestQueue(req);
    }

    public static void setName(BrewPi brewpi, final RequestArrayCallback<BrewPi> callback) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callback.getApplicationContext());
        String apiUrl = prefs.getString("pref_api_server_url", "");
        Boolean allowAllCerts = prefs.getBoolean("pref_api_allow_all_certs", false);

        if (allowAllCerts)
            HttpsTrustManager.allowAllSSL();
        else
            HttpsTrustManager.allowOnlyValidSSL();

        if (apiUrl.isEmpty()) {
            callback.onRequestFailure(0, callback.getApplicationContext()
                    .getString(R.string.error_settings_api_url_missing));
            return;
        }

        String url = String.format(brewpiDetail, apiUrl, brewpi.device_id, "update");

        try {
            Log.d(TAG, BrewPi.toJson(brewpi));
            JSONObject brewpiJsonObject = new JSONObject(BrewPi.toJson(brewpi));

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, url, brewpiJsonObject,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onRequestSuccessful();
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, error.getMessage(), error);

                            String errorMessage = error.getMessage();
                            if (error.networkResponse != null && error.networkResponse.data.length > 0) {
                                try {
                                    errorMessage = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject jsonResonse = new JSONObject(errorMessage);
                                    errorMessage = jsonResonse.getString("Message");
                                }
                                catch (UnsupportedEncodingException e) {
                                    Log.d(TAG, e.getMessage(), e);
                                }
                                catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }

                            callback.onRequestFailure(
                                    error.networkResponse == null ? 0 : error.networkResponse.statusCode,
                                    errorMessage);
                        }
                    });

            req.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, 0, 0));
            OinkbrewApplication.getInstance().addToRequestQueue(req);
        } catch (JSONException error) {
            Log.d(TAG, error.getMessage(), error);
            callback.onRequestFailure(0, error.getMessage());
        }
    }
}
