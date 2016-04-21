package info.vhowto.oinkbrewmobile.remote;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.OinkbrewApplication;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.helpers.HttpsTrustManager;

public class ConfigurationRequest {

    private static final String TAG = ConfigurationRequest.class.getSimpleName();
    private static String configsGeneral = "%s/configs/?archived=%s&all_phases=%s";
    private static String configsBrewPi = "%s/configs/%s/?archived=%s&all_phases=%s";
    private static String configsDedicated = "%s/configs/%s/%d/?archived=%s&all_phases=%s";

    public static void GetConfigurations(final RequestCallback callback, Boolean loadArchived) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callback.getApplicationContext());
        String apiUrl = prefs.getString("pref_api_server_url", "");
        Boolean allowAllCerts = prefs.getBoolean("pref_api_allow_all_certs", false);

        if (allowAllCerts)
            HttpsTrustManager.allowAllSSL();
        else
            HttpsTrustManager.allowOnlyValidSSL();

        if (apiUrl.isEmpty()) {
            callback.onRequestFailure("API Url not set");
            return;
        }

        String url = String.format(configsGeneral, apiUrl, loadArchived.toString(), Boolean.FALSE.toString());

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        ArrayList<Configuration> configurations = new ArrayList<>();

                        if (response.length() > 0) {

                            // looping through json and adding to movies list
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
                        Log.e(TAG, "Server Error: " + error.getMessage());
                        callback.onRequestFailure(error.getMessage());
                    }
                });

        OinkbrewApplication.getInstance().addToRequestQueue(req);
    }
}
