package info.vhowto.oinkbrewmobile.remote;

import android.content.Context;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.domain.Configuration;

public interface RequestCallback {
    void onRequestSuccessful(ArrayList<Configuration> configurations);
    void onRequestFailure(String errorMessage);
    Context getApplicationContext();
}
