package info.vhowto.oinkbrewmobile.remote;

import android.content.Context;

import java.util.ArrayList;

public interface RequestArrayCallback<Entity> {
    void onRequestSuccessful();
    void onRequestSuccessful(ArrayList<Entity> items);
    void onRequestFailure(int statusCode, String errorMessage);
    Context getApplicationContext();
}
