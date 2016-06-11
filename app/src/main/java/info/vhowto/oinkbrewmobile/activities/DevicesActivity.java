package info.vhowto.oinkbrewmobile.activities;

import android.support.v7.app.AppCompatActivity;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.fragments.DeviceListFragment;

public abstract class DevicesActivity extends AppCompatActivity {

    public abstract String getDeviceId();

    public void refreshDevices() {
        DeviceListFragment fragment = (DeviceListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.device_list_fragment);

        fragment.fetchDevices();
    }

}
