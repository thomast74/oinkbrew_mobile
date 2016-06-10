package info.vhowto.oinkbrewmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.adapters.ConfigurationListAdapter;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.domain.ConfigurationType;
import info.vhowto.oinkbrewmobile.fragments.OinkbrewDrawer;
import info.vhowto.oinkbrewmobile.remote.ConfigurationRequest;
import info.vhowto.oinkbrewmobile.remote.RequestArrayCallback;

public class ConfigurationListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RequestArrayCallback<Configuration> {

    private static final String TAG = ConfigurationListActivity.class.getSimpleName();

    private ArrayList<Configuration> configurations;
    private RecyclerView recyclerView;
    private ConfigurationListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Menu menu;
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.configuration_toolbar);
        toolbar.setTitle(R.string.drawer_configurations);
        setSupportActionBar(toolbar);

        drawer = new OinkbrewDrawer().createDrawer(this, toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity host = (Activity) view.getContext();
                Intent intent = new Intent(host, ConfigurationNewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                host.startActivity(intent);
            }
        });

        configurations = new ArrayList<>();

        adapter = new ConfigurationListAdapter(configurations);
        adapter.setItemListener(new ConfigurationListAdapter.ItemListener() {
            @Override
            public void onItemClick(Configuration item) { ConfigurationListActivity.this.onItemClick(item); }
        });
        RecyclerView.LayoutManager llm = new LinearLayoutManager(getApplicationContext());

        recyclerView = (RecyclerView)findViewById(R.id.configuration_recycler_view);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                // show alert to ask to really want to archive
                // if yes
                Configuration configuration = configurations.get(viewHolder.getAdapterPosition());

                // ConfigurationRequest.archive(configuration);
                // if positive archived remove from list
                // if not show error message
                configurations.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.configuration_list_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                fetchConfigurations();
            }
        });


    }

    private void fetchConfigurations() {
        swipeRefreshLayout.setRefreshing(true);

        Boolean loadArchived = !(menu == null || !menu.findItem(R.id.action_archived).isChecked());

        configurations.clear();
        adapter.notifyDataSetChanged();

        ConfigurationRequest.getConfigurations(this, loadArchived);
    }

    public void onRequestSuccessful() {
        adapter.notifyDataSetChanged();
    }

    public void onRequestSuccessful(ArrayList<Configuration> newConfigurations) {
        this.configurations.addAll(newConfigurations);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void onRequestFailure(int statusCode, String errorMessage) {
        switch (statusCode) {
            case 404:
                Toast.makeText(getApplicationContext(), getString(R.string.error_configurations_empty), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void onItemClick(Configuration item) {
        Intent intent = null;

        if (ConfigurationType.BREW.equals(item.type)) {
            intent = new Intent(this, ConfigurationBrewOperationActivity.class);
        }
        else if (ConfigurationType.FERMENTATION.equals(item.type)) {
            intent = new Intent(this, ConfigurationFermentationOperationActivity.class);
        }

        if (intent != null) {
            intent.putExtra("item", item);
            this.startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
