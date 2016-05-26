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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.OinkbrewApplication;
import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Device;
import info.vhowto.oinkbrewmobile.helpers.HttpsTrustManager;

public class DeviceRequest {

    private static final String TAG = DeviceRequest.class.getSimpleName();
    private static final String devicesGeneral = "%s/devices/";
    private static final String devicesBrewPi = "%s/devices/%s/";

    public static void getDevices(String device_id, final RequestArrayCallback callback) {

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

        String url = String.format(device_id == null ? devicesGeneral : devicesBrewPi, apiUrl);

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        ArrayList<Device> devices = new ArrayList<>();

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    Device device = Device.fromJson(response.getJSONObject(i).toString());
                                    devices.add(device);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }
                        }
                        callback.onRequestSuccessful(devices);
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
}