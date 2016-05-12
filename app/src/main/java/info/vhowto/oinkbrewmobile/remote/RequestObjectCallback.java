package info.vhowto.oinkbrewmobile.remote;

import android.content.Context;

public interface RequestObjectCallback<Entity> {
    void onRequestSuccessful();
    void onRequestSuccessful(Entity item);
    void onRequestFailure(int statusCode, String errorMessage);
    Context getApplicationContext();
}
