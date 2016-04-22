package info.vhowto.oinkbrewmobile.remote;

import android.content.Context;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.domain.Configuration;

public interface RequestCallback<Entity> {
    void onRequestSuccessful(ArrayList<Entity> configurations);
    void onRequestFailure(int statusCode, String errorMessage);
    Context getApplicationContext();
}
