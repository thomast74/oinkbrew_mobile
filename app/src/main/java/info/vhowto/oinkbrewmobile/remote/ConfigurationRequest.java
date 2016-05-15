package info.vhowto.oinkbrewmobile.remote;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.helpers.HttpsTrustManager;

public class ConfigurationRequest {

    private static final String TAG = ConfigurationRequest.class.getSimpleName();
    private static final String configsGeneral = "%s/configs/?archived=%s&all_phases=%s";
    private static final String configsBrewPi = "%s/configs/%s/?archived=%s&all_phases=%s";
    private static final String configsDedicated = "%s/configs/%s/%d/";

    public static void getConfigurations(final RequestArrayCallback callback, Boolean loadArchived) {

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

        String url = String.format(configsGeneral, apiUrl, loadArchived, false);

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        ArrayList<Configuration> configurations = new ArrayList<>();

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    Configuration configuration = Configuration.fromJson(response.getJSONObject(i).toString());
                                    configurations.add(configuration);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }
                        }
                        callback.onRequestSuccessful(configurations);
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
                            }
                            catch (UnsupportedEncodingException e) {
                                Log.d(TAG, e.getMessage(), e);
                            }
                        }

                        callback.onRequestFailure(
                                error.networkResponse == null ? 0 : error.networkResponse.statusCode,
                                errorMessage);
                    }
                });

        OinkbrewApplication.getInstance().addToRequestQueue(req);
    }

    public static void updateConfiguration(Configuration configuration, final RequestObjectCallback callback) {
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

        String url = String.format(configsDedicated, apiUrl, configuration.brewpi.device_id, configuration.pk);
        configuration.phases = null;
        Log.d(TAG, Configuration.toJson(configuration));

        try {
            JSONObject configurationJson = new JSONObject(Configuration.toJson(configuration));

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, url, configurationJson,

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
                                }
                                catch (UnsupportedEncodingException e) {
                                    Log.d(TAG, e.getMessage(), e);
                                }
                            }

                            callback.onRequestFailure(
                                    error.networkResponse == null ? 0 : error.networkResponse.statusCode,
                                    errorMessage);
                        }
                    });

            OinkbrewApplication.getInstance().addToRequestQueue(req);
        } catch (JSONException error) {
            Log.d(TAG, error.getMessage(), error);
            callback.onRequestFailure(0, error.getMessage());
        }
    }
}
