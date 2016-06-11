package info.vhowto.oinkbrewmobile.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.Drawer;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.fragments.OinkbrewDrawer;

public class DevicesListActivity extends DevicesActivity {

    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        Toolbar toolbar = (Toolbar) findViewById(R.id.devices_toolbar);
        toolbar.setTitle(R.string.drawer_devices);
        setSupportActionBar(toolbar);

        drawer = new OinkbrewDrawer().createDrawer(this, toolbar);
    }

    @Override
    public String getDeviceId() { return null; }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                refreshDevices();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

}
