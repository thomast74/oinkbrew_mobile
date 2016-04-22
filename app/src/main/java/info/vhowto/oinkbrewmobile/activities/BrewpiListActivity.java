package info.vhowto.oinkbrewmobile.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.adapters.BrewPiAdapter;
import info.vhowto.oinkbrewmobile.domain.BrewPi;
import info.vhowto.oinkbrewmobile.fragments.DrawerHelper;
import info.vhowto.oinkbrewmobile.remote.RequestCallback;

public class BrewPiListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RequestCallback<BrewPi> {

    private ArrayList<BrewPi> brewpis;
    private RecyclerView recyclerView;
    private BrewPiAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Drawer drawer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brewpi_list);

        brewpis = new ArrayList<>();
        adapter = new BrewPiAdapter(brewpis);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());

        recyclerView = (RecyclerView)findViewById(R.id.brewpi_recycler_view);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.brewpi_list_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.brewpi_toolbar);
        setSupportActionBar(toolbar);

        drawer = new DrawerHelper().createDrawer(this, toolbar);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fetchBrewPis();
                                    }
                                });
    }

    private void fetchBrewPis() {
        swipeRefreshLayout.setRefreshing(true);

        brewpis.clear();
        adapter.notifyDataSetChanged();
    }

    public void onRequestSuccessful(ArrayList<BrewPi> newBrewPis) {
        this.brewpis.addAll(newBrewPis);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void onRequestFailure(int statusCode, String errorMessage) {
        switch (statusCode) {
            case 404:
                Toast.makeText(getApplicationContext(), getString(R.string.error_brewpis_empty), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_brewpi_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                fetchBrewPis();
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
        fetchBrewPis();
    }
}
