package info.vhowto.oinkbrewmobile.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.domain.Log;
import info.vhowto.oinkbrewmobile.domain.Phase;
import info.vhowto.oinkbrewmobile.helpers.TempAxisValueFormatter;
import info.vhowto.oinkbrewmobile.remote.ConfigurationRequest;
import info.vhowto.oinkbrewmobile.remote.LogRequest;
import info.vhowto.oinkbrewmobile.remote.RequestObjectCallback;

public class ConfigurationFermentationOperationActivity extends AppCompatActivity implements RequestObjectCallback<Log> {

    private static final String TAG = ConfigurationFermentationOperationActivity.class.getSimpleName();
    private static long TIMER_PERIOD = 60000;

    private Configuration configuration;
    private Menu menu;
    private LineChart chart;
    private int limit = 3;
    private Handler handler;
    private Runnable runnable;
    private ConfigurationOperationViewHolder viewHolder;
    private ProgressDialog progress;

    private static class ConfigurationOperationViewHolder {
        TextView target;
        TextView fridge;
        TextView beer_1;
        TextView beer_2;
        TextView lbl_beer_1;
        TextView lbl_beer_2;

        int black;
        int white;
        int red;
        int green;
        int green_light;
        int amber;
        int grey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_fermentation_operation);

        configuration = (Configuration) getIntent().getSerializableExtra("item");

        Toolbar toolbar = (Toolbar) findViewById(R.id.configuration_fermentation_operation_toolbar);
        toolbar.setTitle(configuration.name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewHolder = new ConfigurationOperationViewHolder();
        viewHolder.target = (TextView) findViewById(R.id.configuration_target);
        viewHolder.fridge = (TextView) findViewById(R.id.configuration_fridge);
        viewHolder.beer_1 = (TextView) findViewById(R.id.configuration_beer_1);
        viewHolder.beer_2 = (TextView) findViewById(R.id.configuration_beer_2);
        viewHolder.lbl_beer_1 = (TextView) findViewById(R.id.lbl_configuration_beer_1);
        viewHolder.lbl_beer_2 = (TextView) findViewById(R.id.lbl_configuration_beer_2);
        viewHolder.black = getColor(R.color.black);
        viewHolder.white = getColor(R.color.white);
        viewHolder.red = getColor(R.color.md_red_500);
        viewHolder.green = getColor(R.color.md_green_500);
        viewHolder.green_light = getColor(R.color.md_green_200);
        viewHolder.amber = getColor(R.color.md_amber_500);
        viewHolder.grey = getColor(R.color.md_grey_300);

        if (configuration.temp_sensor.contains("Beer 1")) {
            viewHolder.lbl_beer_1.setBackgroundColor(viewHolder.green_light);
            viewHolder.lbl_beer_1.setTextColor(viewHolder.white);
            viewHolder.lbl_beer_2.setBackgroundColor(viewHolder.grey);
            viewHolder.lbl_beer_2.setTextColor(viewHolder.black);
        } else if (configuration.temp_sensor.contains("Beer 2")) {
            viewHolder.lbl_beer_2.setBackgroundColor(viewHolder.green_light);
            viewHolder.lbl_beer_2.setTextColor(viewHolder.white);
            viewHolder.lbl_beer_1.setBackgroundColor(viewHolder.grey);
            viewHolder.lbl_beer_1.setTextColor(viewHolder.black);
        }

        if (!configuration.archived) {
            TextView targetCard = (TextView) findViewById(R.id.configuration_target);
            targetCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adjustTargetTemperature();
                }
            });
        }

        chart = configureChart();
        fetchLogData();

        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.updating));
        progress.setMessage(getString(R.string.updating_message));
        progress.setCancelable(false);

        if (!configuration.archived) {
            startTimer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchLogData();

        if (menu.findItem(R.id.action_refresh_automatically).isChecked() && !configuration.archived)
            startTimer();
    }

    private LineChart configureChart() {
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

        return chart;
    }

    private void fetchLogData() {
        LogRequest.getLogs(configuration.brewpi.device_id, configuration.pk, limit, this);
    }

    public void onRequestSuccessful() {
        progress.dismiss();
        Toast.makeText(getApplicationContext(), R.string.configuration_target_update_success, Toast.LENGTH_LONG).show();
    }

    public void onRequestSuccessful(Log item) {
        progress.dismiss();

        SimpleDateFormat ft = new SimpleDateFormat ("HH:mm");
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> targetTempData = new ArrayList<>();
        ArrayList<Entry> fridgeTempData = new ArrayList<>();
        ArrayList<Entry> beer1TempData = new ArrayList<>();
        ArrayList<Entry> coolingData = new ArrayList<>();
        ArrayList<Entry> heatData = new ArrayList<>();

        for(int i=0; i < item.points.length; i++) {
            xVals.add(ft.format(item.points[i].time));
            targetTempData.add(new Entry(item.points[i].Target, i));
            fridgeTempData.add(new Entry(item.points[i].Fridge, i));
            beer1TempData.add(new Entry(item.points[i].Beer_1, i));
            coolingData.add(new Entry(item.points[i].Cooling, i));
            heatData.add(new Entry(item.points[i].Heating, i));
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

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(coolingDataSet);
        dataSets.add(heatingDataSet);
        dataSets.add(targetTempDataSet);
        dataSets.add(fridgeTempDataSet);
        dataSets.add(beer1TempDataSet);

        LineData lineData = new LineData(xVals, dataSets);
        chart.setData(lineData);
        chart.notifyDataSetChanged();
        chart.invalidate();

        if (item.points.length > 0) {
            viewHolder.target.setText(String.format("%.2f ºC", item.points[item.points.length - 1].Target));
            viewHolder.fridge.setText(String.format("%.2f ºC", item.points[item.points.length - 1].Fridge));
            viewHolder.beer_1.setText(String.format("%.2f ºC", item.points[item.points.length - 1].Beer_1));
            viewHolder.beer_2.setText(String.format("%.2f ºC", item.points[item.points.length - 1].Beer_2));

            if (item.points[item.points.length - 1].Beer_1 > 0) {
                float beer_1_error = item.points[item.points.length - 1].Beer_1 - item.points[item.points.length - 1].Target;
                if (beer_1_error < 0)
                    beer_1_error *= -1;

                if (beer_1_error > 0.5)
                    viewHolder.beer_1.setTextColor(viewHolder.red);
                else if (beer_1_error > 0.149)
                    viewHolder.beer_1.setTextColor(viewHolder.amber);
                else
                    viewHolder.beer_1.setTextColor(viewHolder.green);
            }
        }
        else {
            viewHolder.target.setText("--.-- ºC");
            viewHolder.fridge.setText("--.-- ºC");
            viewHolder.beer_1.setText("--.-- ºC");
            viewHolder.beer_2.setText("--.-- ºC");
            viewHolder.beer_1.setTextColor(viewHolder.black);
            viewHolder.beer_2.setTextColor(viewHolder.black);
        }
    }

    public void onRequestFailure(int statusCode, String errorMessage) {
        progress.dismiss();
        final ConfigurationFermentationOperationActivity activity = this;

        switch (statusCode) {
            case 400:
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(getString(R.string.error));
                builder.setMessage(errorMessage);
                builder.setCancelable(false);

                builder.setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    progress.show();
                    ConfigurationRequest.update(configuration.clone(), activity);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.show();
            case 404:
                Toast.makeText(getApplicationContext(), getString(R.string.error_log_empty), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_configuration_fermentation_operation, menu);

        if (configuration.archived) {
            menu.findItem(R.id.action_refresh).setEnabled(false);
            menu.findItem(R.id.action_refresh_automatically).setChecked(false).setEnabled(false);
        } else {
            menu.findItem(R.id.action_refresh).setEnabled(true);
            menu.findItem(R.id.action_refresh_automatically).setEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean result = false;

        switch (id) {
            case android.R.id.home:
                stopTimer();
                super.onBackPressed();
                finish();
                result = true;
                break;
            case R.id.action_refresh:
                fetchLogData();
                result = true;
                break;
            case R.id.action_refresh_automatically:
                if (item.isChecked()) {
                    item.setChecked(false);
                    stopTimer();
                }
                else {
                    item.setChecked(true);
                    startTimer();
                }
                result = true;
                break;
            case R.id.action_last_3_hours:
                limit = 3;
                fetchLogData();
                result = true;
                break;
            case R.id.action_last_6_hours:
                limit = 6;
                fetchLogData();
                result = true;
                break;
            case R.id.action_last_12_hours:
                limit = 12;
                fetchLogData();
                result = true;
                break;
        }

        return result;
    }

    private void startTimer() {
        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    fetchLogData();
                    handler.postDelayed(this, TIMER_PERIOD);
                }
            };
        }
        handler.postDelayed(runnable, TIMER_PERIOD);
    }

    private void stopTimer() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void adjustTargetTemperature() {

        final ConfigurationFermentationOperationActivity callback = this;
        View view = getLayoutInflater().inflate(R.layout.content_target_temperature, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_change_target_temp);
        builder.setView(view);

        Float currentTargetTemp = Float.parseFloat(viewHolder.target.getText().subSequence(0,viewHolder.target.getText().length() - 3).toString());
        final NumberPicker mainNumber = (NumberPicker)view.findViewById(R.id.mainNumber);
        final NumberPicker decimalNumber = (NumberPicker)view.findViewById(R.id.decimalNumber);

        mainNumber.setMinValue(0);
        mainNumber.setMaxValue(100);
        mainNumber.setValue(currentTargetTemp.intValue());
        decimalNumber.setMinValue(0);
        decimalNumber.setMaxValue(9);
        decimalNumber.setValue((int)Math.round((currentTargetTemp % 1) * 10));

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callback.getApplicationContext());
                Float newTargetTemp = Float.parseFloat(mainNumber.getValue() + "." + decimalNumber.getValue());

                configuration.phase = new Phase();
                configuration.phase.temperature = newTargetTemp;
                configuration.phase.fan_pwm = Float.parseFloat(prefs.getString("pref_fermentation_fan_pwm", "100.0"));
                configuration.phase.heating_period = Long.parseLong(prefs.getString("pref_fermentation_heat_period", "1000"));
                configuration.phase.cooling_period = Long.parseLong(prefs.getString("pref_fermentation_cooling_period", "600000"));
                configuration.phase.cooling_on_time = Long.parseLong(prefs.getString("pref_fermentation_cooling_on_time", "150000"));
                configuration.phase.cooling_off_time = Long.parseLong(prefs.getString("pref_fermentation_cooling_off_time", "180000"));
                configuration.phase.p = Float.parseFloat(prefs.getString("pref_fermentation_p", "18.0"));
                configuration.phase.i = Float.parseFloat(prefs.getString("pref_fermentation_i", "0.0001"));
                configuration.phase.d = Float.parseFloat(prefs.getString("pref_fermentation_d", "-8.0"));

                progress.show();
                ConfigurationRequest.update(configuration.clone(), callback);
            }
        });

        builder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
