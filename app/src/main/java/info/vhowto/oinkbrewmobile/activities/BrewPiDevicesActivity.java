package info.vhowto.oinkbrewmobile.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import info.vhowto.oinkbrewmobile.R;

public class BrewPiDevicesActivity extends DevicesActivity {

    private String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brewpi_devices);

        device_id = getIntent().getStringExtra("device_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.brewpi_devices_toolbar);
        toolbar.setTitle(R.string.drawer_devices);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public String getDeviceId() {
        return device_id;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                refreshDevices();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
