package info.vhowto.oinkbrewmobile.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.vhowto.oinkbrewmobile.DrawerHelper;
import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.adapters.ConfigurationListAdapter;
import info.vhowto.oinkbrewmobile.domain.Configuration;

public class ConfigurationListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private ConfigurationListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Drawer drawer = null;

    private List<Configuration> configurations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_list);

        configurations = new ArrayList<Configuration>();
        configurations.add(new Configuration("Barbatos", new Date(2016, 01, 30, 7, 0)));
        configurations.add(new Configuration("Jasmin IPA", new Date(2016, 02, 18, 8, 0)));

        listView = (ListView) findViewById(R.id.configuration_list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.configuration_list_swipe_refresh_layout);
        adapter = new ConfigurationListAdapter(this, configurations);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = new DrawerHelper().createDrawer(this, toolbar);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fetchConfigurations();
                                    }
                                }
        );
    }

    private void fetchConfigurations() {
        swipeRefreshLayout.setRefreshing(true);

        // get archive flag
        // load data from API request

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configuration_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                fetchConfigurations();
                return true;
            case R.id.action_archived:
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                fetchConfigurations();
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

    @Override
    public void onRefresh() {
        fetchConfigurations();
    }
}
