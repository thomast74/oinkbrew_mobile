package info.vhowto.oinkbrewmobile.activities;

import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.Drawer;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.fragments.OinkbrewDrawer;

public class ConfigurationFermentationOperationActivity extends AppCompatActivity {

    private Configuration configuration;
    private Menu menu;
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_fermentation_operation);

        configuration = (Configuration)getIntent().getSerializableExtra("item");

        Toolbar toolbar = (Toolbar) findViewById(R.id.configuration_fermentation_operation_toolbar);
        toolbar.setTitle(configuration.name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configureChart();
    }

    private void configureChart() {
        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.setAutoScaleMinMaxEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinValue(0);
        leftAxis.setAxisMaxValue(100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_configuration_fermentation_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
