package info.vhowto.oinkbrewmobile.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.Drawer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.fragments.OinkbrewDrawer;
import info.vhowto.oinkbrewmobile.helpers.TempAxisValueFormatter;

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
        chart.setDescription("");

        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_400));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setYOffset(10F);
        xAxis.setAxisLineColor(Color.BLACK);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinValue(0);
        leftAxis.setAxisMaxValue(100);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_400));
        rightAxis.setGranularity(0.1F);
        rightAxis.setGranularityEnabled(true);
        rightAxis.setValueFormatter(new TempAxisValueFormatter());

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> targetTempData = new ArrayList<Entry>();
        ArrayList<Entry> fridgeTempData = new ArrayList<Entry>();
        ArrayList<Entry> beer1TempData = new ArrayList<Entry>();
        ArrayList<Entry> coolingData = new ArrayList<Entry>();
        ArrayList<Entry> heatData = new ArrayList<Entry>();

        float targetTemp = 14F;
        float fridgeTemp = 19F;
        float beer1Temp = 16F;
        double cooling = 0.0F;
        float heat = 0.0F;
        int fridgeDirection = -1;
        int beerDirection = -1;
        boolean isCooling = true;
        int cooling_direction = 1;

        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm");
        GregorianCalendar date = new GregorianCalendar();
        date.add(Calendar.HOUR, -3);

        for(int i=0; i < 180; i++) {

            xVals.add(ft.format(date.getTime()));
            date.add(Calendar.MINUTE, 1);

            targetTempData.add(new Entry(targetTemp, i));
            fridgeTempData.add(new Entry(fridgeTemp, i));
            beer1TempData.add(new Entry(beer1Temp, i));

            if (fridgeTemp < 10)
                fridgeDirection = 1;
            else if (fridgeTemp > 18)
                fridgeDirection = -1;

            if (beer1Temp < 13.8)
                beerDirection = 1;
            else if (beer1Temp > 14.2)
                beerDirection = -1;

            if (isCooling && cooling > 20)
                cooling_direction = -1;
            else if (isCooling && cooling < 0) {
                isCooling = false;
                cooling_direction = 1;
            }
            else if (!isCooling && heat > 19)
                cooling_direction = -1;
            else if (!isCooling && heat < 0) {
                isCooling = true;
                cooling_direction = 1;
            }

            if (isCooling) {
                if (cooling_direction == 1)
                    cooling += 0.5;
                else
                    cooling -= 0.5;
            }
            else {
                if (cooling_direction == 1)
                    heat += 0.3;
                else
                    heat -= 0.3;
            }

            coolingData.add(new Entry((float)Math.log10(cooling)*34, i));
            heatData.add(new Entry(heat, i));


            fridgeTemp = fridgeDirection < 0 ? (fridgeTemp - 0.2F) : (fridgeTemp + 0.2F);
            beer1Temp = beerDirection < 0 ? (beer1Temp - 0.063F) : (beer1Temp + 0.063F);
        }

        LineDataSet coolingDataSet = new LineDataSet(coolingData, "Cooling");
        LineDataSet heatingDataSet = new LineDataSet(heatData, "Heating");
        LineDataSet targetTempDataSet = new LineDataSet(targetTempData, "Target");
        LineDataSet fridgeTempDataSet = new LineDataSet(fridgeTempData, "Fridge Inside");
        LineDataSet beer1TempDataSet = new LineDataSet(beer1TempData, "Beer 1");

        coolingDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        coolingDataSet.setDrawCircles(false);
        coolingDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_cooling));
        coolingDataSet.setLineWidth(1.0f);

        heatingDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        heatingDataSet.setDrawCircles(false);
        heatingDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_heating));
        heatingDataSet.setLineWidth(1.0f);

        targetTempDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        targetTempDataSet.setDrawCircles(false);
        targetTempDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_target));
        targetTempDataSet.setLineWidth(1.5f);

        fridgeTempDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        fridgeTempDataSet.setDrawCircles(false);
        fridgeTempDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_fridge));
        fridgeTempDataSet.setLineWidth(1.5f);

        beer1TempDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        beer1TempDataSet.setDrawCircles(false);
        beer1TempDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_beer_1));
        beer1TempDataSet.setLineWidth(2.5f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(coolingDataSet);
        dataSets.add(heatingDataSet);
        dataSets.add(targetTempDataSet);
        dataSets.add(fridgeTempDataSet);
        dataSets.add(beer1TempDataSet);

        LineData lineData = new LineData(xVals, dataSets);
        chart.setData(lineData);
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
