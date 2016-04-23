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
import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.BrewPi;
import info.vhowto.oinkbrewmobile.helpers.HttpsTrustManager;

public class BrewPiRequest {

    private static final String TAG = ConfigurationRequest.class.getSimpleName();
    private static final String brewpisGeneral = "%s/brewpis/";
    private static final String brewpiDetail = "%s/configs/%s/%s";

    public static void getBrewPis(final RequestCallback callback) {

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
                        callback.onRequestFailure(error.networkResponse == null ? 0 : error.networkResponse.statusCode,
                                error.getMessage());
                    }
                });

        OinkbrewApplication.getInstance().addToRequestQueue(req);
    }

}
