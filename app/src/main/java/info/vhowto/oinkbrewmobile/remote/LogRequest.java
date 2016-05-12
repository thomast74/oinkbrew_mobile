package info.vhowto.oinkbrewmobile.remote;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import info.vhowto.oinkbrewmobile.OinkbrewApplication;
import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Log;
import info.vhowto.oinkbrewmobile.helpers.HttpsTrustManager;

public class LogRequest {

    private static final String TAG = LogRequest.class.getSimpleName();
    private static final String logsGeneral = "%s/logs/?limit=%s";
    private static final String logsDetail = "%s/logs/%s/%d/?limit=%s";

    public static void getLogs(String device_id, int configuration_id, int limit, final RequestObjectCallback callback)  {

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

        String url = String.format(logsDetail, apiUrl, device_id, configuration_id, limit);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        android.util.Log.d(TAG, response.toString());
                        Log log = Log.fromJson(response.toString());
                        callback.onRequestSuccessful(log);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onRequestFailure(error.networkResponse == null ? 0 : error.networkResponse.statusCode, error.getMessage());
                    }
                });

        OinkbrewApplication.getInstance().addToRequestQueue(req);
    }
}
