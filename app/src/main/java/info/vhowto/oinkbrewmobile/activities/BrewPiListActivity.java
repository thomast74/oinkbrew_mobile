package info.vhowto.oinkbrewmobile.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.adapters.BrewPiListAdapter;
import info.vhowto.oinkbrewmobile.domain.BrewPi;
import info.vhowto.oinkbrewmobile.fragments.OinkbrewDrawer;
import info.vhowto.oinkbrewmobile.remote.BrewPiRequest;
import info.vhowto.oinkbrewmobile.remote.RequestArrayCallback;

public class BrewPiListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RequestArrayCallback<BrewPi> {

    private ArrayList<BrewPi> brewpis;
    private RecyclerView recyclerView;
    private BrewPiListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brewpi_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.brewpi_toolbar);
        toolbar.setTitle(R.string.drawer_brewpis);
        setSupportActionBar(toolbar);

        drawer = new OinkbrewDrawer().createDrawer(this, toolbar);

        brewpis = new ArrayList<>();

        adapter = new BrewPiListAdapter(brewpis);
        adapter.setItemListener(new BrewPiListAdapter.ItemListener() {
            @Override
            public void onMenuItemClicked(BrewPi item, MenuItem menuItem) { onCardMenuItemClick(item, menuItem); }
            @Override
            public void onItemClick(BrewPi item) { BrewPiListActivity.this.onItemClick(item); }
        });
        RecyclerView.LayoutManager llm = new LinearLayoutManager(getApplicationContext());

        recyclerView = (RecyclerView)findViewById(R.id.brewpi_recycler_view);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.brewpi_list_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
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

        BrewPiRequest.getBrewPis(this);
    }

    public void onRequestSuccessful() {
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

    private void onItemClick(BrewPi item) {
        Toast.makeText(this, item.name + " selected", Toast.LENGTH_LONG).show();
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

    public void onCardMenuItemClick(final BrewPi item, MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (item == null)
            return;

        switch (id) {
            case R.id.action_change_name:
                final BrewPiListActivity callback = this;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.action_change_name);
                final EditText input = new EditText(this);
                input.setText(item.name);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        item.name = input.getText().toString();
                        BrewPiRequest.setName(item, callback);
                    }
                });
                builder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            case R.id.action_reset:
                // TODO: Implement BrewPi Reset
                Toast.makeText(getApplicationContext(),"Reset: " + item.name, Toast.LENGTH_LONG).show();
                break;
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
