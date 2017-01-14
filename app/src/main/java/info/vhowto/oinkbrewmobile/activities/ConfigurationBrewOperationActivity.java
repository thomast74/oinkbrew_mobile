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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
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
import info.vhowto.oinkbrewmobile.domain.LogPoint;
import info.vhowto.oinkbrewmobile.domain.Phase;
import info.vhowto.oinkbrewmobile.domain.TempSensorSelection;
import info.vhowto.oinkbrewmobile.helpers.TempAxisValueFormatter;
import info.vhowto.oinkbrewmobile.remote.ConfigurationRequest;
import info.vhowto.oinkbrewmobile.remote.LogRequest;
import info.vhowto.oinkbrewmobile.remote.RequestObjectCallback;

public class ConfigurationBrewOperationActivity extends AppCompatActivity implements RequestObjectCallback<Log> {

    private static long TIMER_PERIOD = 5000;

    private Configuration configuration;
    private Menu menu;
    private LineChart chart;
    private int limit = 15;
    private Handler handler;
    private Runnable runnable;
    private ConfigurationOperationViewHolder viewHolder;
    private ProgressDialog progress;


    private static class ConfigurationOperationViewHolder {
        TextView target;
        TextView hlt_in;
        TextView hlt_out;
        TextView mash_in;
        TextView mash_out;
        TextView boil_in;
        TextView boil_out;

        TextView lbl_hlt;
        TextView lbl_mash;
        TextView lbl_boil;

        CardView hlt;
        CardView mash;
        CardView boil;

        SeekBar water_pump;
        SeekBar wort_pump;

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
        setContentView(R.layout.activity_configuration_brew_operation);

        configuration = (Configuration)getIntent().getSerializableExtra("item");
        if (configuration.phases != null && configuration.phases.length > 0) {
            configuration.phase = configuration.phases[0];
        }
        else {
            configuration.phase = new Phase();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.configuration_brew_operation_toolbar);
        toolbar.setTitle(configuration.name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configureViewHolder();
        configureChart();
        updateActiveTempSensorCard();
        updatePumps();
        fetchLogData();

        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.updating));
        progress.setMessage(getString(R.string.updating_message));
        progress.setCancelable(false);

        if (!configuration.archived) {
            startTimer();

            viewHolder.target.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adjustTarget();
                }
            });
            viewHolder.water_pump.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar pump, int progress, boolean fromUser) {
                    if (fromUser)
                        adjustPumpSettings(pump);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            viewHolder.wort_pump.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar pump, int progress, boolean fromUser) {
                    if (fromUser)
                        adjustPumpSettings(pump);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            viewHolder.hlt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!configuration.temp_sensor.contains("HLT")) {
                        adjustTempSensor(TempSensorSelection.HLT);
                    }
                }
            });
            viewHolder.mash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!configuration.temp_sensor.contains("Mash")) {
                        adjustTempSensor(TempSensorSelection.MASH);
                    }
                }
            });
            viewHolder.boil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!configuration.temp_sensor.contains("Boil")) {
                        adjustTempSensor(TempSensorSelection.BOIL);
                    }
                }
            });
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

    private void configureViewHolder() {
        viewHolder = new ConfigurationOperationViewHolder();
        viewHolder.target = (TextView) findViewById(R.id.configuration_target);
        viewHolder.hlt_in = (TextView) findViewById(R.id.configuration_hlt_in);
        viewHolder.hlt_out = (TextView) findViewById(R.id.configuration_hlt_out);
        viewHolder.mash_in = (TextView) findViewById(R.id.configuration_mash_in);
        viewHolder.mash_out = (TextView) findViewById(R.id.configuration_mash_out);
        viewHolder.boil_in = (TextView) findViewById(R.id.configuration_boil_in);
        viewHolder.boil_out = (TextView) findViewById(R.id.configuration_boil_out);

        viewHolder.lbl_hlt = (TextView) findViewById(R.id.lbl_configuration_hlt);
        viewHolder.lbl_mash = (TextView) findViewById(R.id.lbl_configuration_mash);
        viewHolder.lbl_boil = (TextView) findViewById(R.id.lbl_configuration_boil);

        viewHolder.hlt = (CardView) findViewById(R.id.configuration_card_hlt);
        viewHolder.mash = (CardView) findViewById(R.id.configuration_card_mash);
        viewHolder.boil = (CardView) findViewById(R.id.configuration_card_boil);

        viewHolder.water_pump = (SeekBar) findViewById(R.id.configuration_water_pump_seekbar);
        viewHolder.wort_pump = (SeekBar) findViewById(R.id.configuration_wort_pump_seekbar);

        viewHolder.black = getColor(R.color.black);
        viewHolder.white = getColor(R.color.white);
        viewHolder.red = getColor(R.color.md_red_500);
        viewHolder.green = getColor(R.color.md_green_500);
        viewHolder.green_light = getColor(R.color.md_green_200);
        viewHolder.amber = getColor(R.color.md_amber_500);
        viewHolder.grey = getColor(R.color.md_grey_300);
    }

    private void configureChart() {
        chart = (LineChart) findViewById(R.id.chart);
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
        ArrayList<Entry> hltOutTempData = new ArrayList<>();
        ArrayList<Entry> mashInTempData = new ArrayList<>();
        ArrayList<Entry> mashOutTempData = new ArrayList<>();
        ArrayList<Entry> boilOutTempData = new ArrayList<>();
        ArrayList<Entry> hltHeatData = new ArrayList<>();
        ArrayList<Entry> boilHeatData = new ArrayList<>();

        for(int i=0; i < item.points.length; i++) {
            xVals.add(ft.format(item.points[i].time));
            targetTempData.add(new Entry(item.points[i].Target, i));
            hltOutTempData.add(new Entry(item.points[i].HLT_Out, i));
            mashInTempData.add(new Entry(item.points[i].Mash_In, i));
            mashOutTempData.add(new Entry(item.points[i].Mash_Out, i));
            boilOutTempData.add(new Entry(item.points[i].Boil_Out, i));
            hltHeatData.add(new Entry(item.points[i].HLT_Heating, i));
            boilHeatData.add(new Entry(item.points[i].Boil_Heating, i));
        }

        LineDataSet hltHeatDataSet = new LineDataSet(hltHeatData, "HLT");
        LineDataSet boilHeatDataSet = new LineDataSet(boilHeatData, "Boil");
        LineDataSet targetTempDataSet = new LineDataSet(targetTempData, "Target");
        LineDataSet hltOutTempDataSet = new LineDataSet(hltOutTempData, "Hlt Out");
        LineDataSet mashInTempDataSet = new LineDataSet(mashInTempData, "Mash In");
        LineDataSet mashOutTempDataSet = new LineDataSet(mashOutTempData, "Mash Out");
        LineDataSet boilOutTempDataSet = new LineDataSet(boilOutTempData, "Boil Out");

        hltHeatDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        hltHeatDataSet.setDrawCircles(false);
        hltHeatDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_hlt_element));
        hltHeatDataSet.setLineWidth(1.0f);

        boilHeatDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        boilHeatDataSet.setDrawCircles(false);
        boilHeatDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_boil_element));
        boilHeatDataSet.setLineWidth(1.0f);

        targetTempDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        targetTempDataSet.setDrawCircles(false);
        targetTempDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_target));
        targetTempDataSet.setLineWidth(1.5f);

        hltOutTempDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        hltOutTempDataSet.setDrawCircles(false);
        hltOutTempDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_hlt_out));
        hltOutTempDataSet.setLineWidth(1.25f);

        mashInTempDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        mashInTempDataSet.setDrawCircles(false);
        mashInTempDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_mash_in));
        mashInTempDataSet.setLineWidth(2.5f);

        mashOutTempDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        mashOutTempDataSet.setDrawCircles(false);
        mashOutTempDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_mash_out));
        mashOutTempDataSet.setLineWidth(2.5f);

        boilOutTempDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        boilOutTempDataSet.setDrawCircles(false);
        boilOutTempDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_boil_out));
        boilOutTempDataSet.setLineWidth(2.5f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(hltHeatDataSet);
        dataSets.add(boilHeatDataSet);
        dataSets.add(targetTempDataSet);
        dataSets.add(hltOutTempDataSet);
        dataSets.add(mashInTempDataSet);
        dataSets.add(mashOutTempDataSet);
        dataSets.add(boilOutTempDataSet);

        LineData lineData = new LineData(xVals, dataSets);
        chart.setData(lineData);
        chart.notifyDataSetChanged();
        chart.invalidate();

        updateTempCards(item.points.length > 0 ? item.points[item.points.length - 1] : null);
    }

    public void onRequestFailure(int statusCode, String errorMessage) {
        progress.dismiss();
        final ConfigurationBrewOperationActivity activity = this;

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
        updateTempCards(null);
    }

    private void updateActiveTempSensorCard() {
        if (configuration.temp_sensor.contains("HLT In") || configuration.temp_sensor.contains("HLT Out")) {
            viewHolder.lbl_hlt.setBackgroundColor(viewHolder.green_light);
            viewHolder.lbl_hlt.setTextColor(viewHolder.white);
            viewHolder.lbl_mash.setBackgroundColor(viewHolder.grey);
            viewHolder.lbl_mash.setTextColor(viewHolder.black);
            viewHolder.lbl_boil.setBackgroundColor(viewHolder.grey);
            viewHolder.lbl_boil.setTextColor(viewHolder.black);
        }
        else if (configuration.temp_sensor.contains("Mash In") || configuration.temp_sensor.contains("Mash Out")) {
            viewHolder.lbl_hlt.setBackgroundColor(viewHolder.grey);
            viewHolder.lbl_hlt.setTextColor(viewHolder.black);
            viewHolder.lbl_mash.setBackgroundColor(viewHolder.green_light);
            viewHolder.lbl_mash.setTextColor(viewHolder.white);
            viewHolder.lbl_boil.setBackgroundColor(viewHolder.grey);
            viewHolder.lbl_boil.setTextColor(viewHolder.black);
        }
        else if (configuration.temp_sensor.contains("Boil In") || configuration.temp_sensor.contains("Boil Out")) {
            viewHolder.lbl_hlt.setBackgroundColor(viewHolder.grey);
            viewHolder.lbl_hlt.setTextColor(viewHolder.black);
            viewHolder.lbl_mash.setBackgroundColor(viewHolder.grey);
            viewHolder.lbl_mash.setTextColor(viewHolder.black);
            viewHolder.lbl_boil.setBackgroundColor(viewHolder.green_light);
            viewHolder.lbl_boil.setTextColor(viewHolder.white);
        }
    }

    private void updatePumps() {
        viewHolder.water_pump.setProgress((int)configuration.phase.pump_1_pwm.intValue());
        viewHolder.wort_pump.setProgress((int)configuration.phase.pump_2_pwm.intValue());
    }

    private void updateTempCards(LogPoint point) {

        if (point != null) {
            if (configuration.temp_sensor.contains("Boil")) {
                viewHolder.target.setText(configuration.phase.heat_pwm.intValue() + "%");
            }
            else {
                viewHolder.target.setText(String.format("%.2f ºC", point.Target));
            }
            viewHolder.hlt_out.setText(String.format("%.2f ºC", point.HLT_Out));
            viewHolder.mash_in.setText(String.format("%.2f ºC", point.Mash_In));
            viewHolder.mash_out.setText(String.format("%.2f ºC", point.Mash_Out));
            viewHolder.boil_out.setText(String.format("%.2f ºC", point.Boil_Out));

            float error = 0;
            TextView target_label = viewHolder.hlt_out;

            if (configuration.temp_sensor.contains("HLT Out")) {
                error = point.HLT_Out - point.Target;
                target_label = viewHolder.hlt_out;
            }
            else if (configuration.temp_sensor.contains("Mash In")) {
                error = point.HLT_Out - point.Target;
                target_label = viewHolder.mash_in;
            }
            else if (configuration.temp_sensor.contains("Mash Out")) {
                error = point.HLT_Out - point.Target;
                target_label = viewHolder.mash_out;
            }
            else if (configuration.temp_sensor.contains("Boil Out")) {
                error = point.HLT_Out - point.Target;
                target_label = viewHolder.boil_out;
            }

            if (error < 0)
                error *= -1;


            if (error > 0.3)
                target_label.setTextColor(viewHolder.red);
            else if (error > 0.149)
                target_label.setTextColor(viewHolder.amber);
            else
                target_label.setTextColor(viewHolder.green);
        }
        else {
            viewHolder.target.setText("--.--");
            viewHolder.hlt_out.setText("--.- ºC");
            viewHolder.mash_in.setText("--.- ºC");
            viewHolder.mash_out.setText("--.- ºC");
            viewHolder.boil_out.setText("--.- ºC");
            viewHolder.hlt_out.setTextColor(viewHolder.black);
            viewHolder.mash_in.setTextColor(viewHolder.black);
            viewHolder.mash_out.setTextColor(viewHolder.black);
            viewHolder.boil_out.setTextColor(viewHolder.black);
        }
    }

    private void adjustTarget() {
        if (configuration.temp_sensor.contains("Boil")) {
            adjustTargetIntensity();
        }
        else {
            adjustTargetTemperature();
        }
    }

    private void adjustTargetIntensity() {
        final ConfigurationBrewOperationActivity callback = this;
        View view = getLayoutInflater().inflate(R.layout.content_target_intensity, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_change_target_intensity);
        builder.setView(view);

        Float currentTargetIntensity = 0F;
        if (!viewHolder.target.getText().toString().contains("C") && !viewHolder.target.getText().toString().contains("--.-") ) {
            currentTargetIntensity = Float.parseFloat(viewHolder.target.getText().toString().replace('%', ' '));
        }

        final NumberPicker intensity = (NumberPicker)view.findViewById(R.id.intensity);
        intensity.setMinValue(0);
        intensity.setMaxValue(100);
        intensity.setValue(currentTargetIntensity.intValue());

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callback.getApplicationContext());

                configuration.phase.temperature = 0.0F;
                configuration.phase.heat_pwm = new Float(intensity.getValue());
                configuration.phase.heating_period = Long.parseLong(prefs.getString("pref_brew_heat_period", "1000"));
                configuration.phase.p = Float.parseFloat(prefs.getString("pref_brew_p", "18.0"));
                configuration.phase.i = Float.parseFloat(prefs.getString("pref_brew_i", "0.0001"));
                configuration.phase.d = Float.parseFloat(prefs.getString("pref_brew_d", "-8.0"));

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

    private void adjustTargetTemperature() {
        final ConfigurationBrewOperationActivity callback = this;
        View view = getLayoutInflater().inflate(R.layout.content_target_temperature, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_change_target_temp);
        builder.setView(view);

        Float currentTargetTemp = 0F;
        if (!viewHolder.target.getText().toString().contains("%") && !viewHolder.target.getText().toString().contains("--.-") ) {
            currentTargetTemp = Float.parseFloat(viewHolder.target.getText().subSequence(0,viewHolder.target.getText().length() - 3).toString());
        }

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

                configuration.phase.temperature = newTargetTemp;
                configuration.phase.heat_pwm = 0.0F;
                configuration.phase.heating_period = Long.parseLong(prefs.getString("pref_brew_heat_period", "1000"));
                configuration.phase.p = Float.parseFloat(prefs.getString("pref_brew_p", "120.0"));
                configuration.phase.i = Float.parseFloat(prefs.getString("pref_brew_i", "0.0001"));
                configuration.phase.d = Float.parseFloat(prefs.getString("pref_brew_d", "-30.0"));

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

    private void adjustTempSensor(TempSensorSelection tempSensor) {
        switch (tempSensor) {
            case HLT:
                configuration.temp_sensor = "HLT Out Temp Sensor";
                configuration.heat_actuator = "HLT Heating Actuator";
                if (viewHolder.target.getText().toString().contains("%")) {
                    configuration.phase.temperature = 0F;
                    configuration.phase.heat_pwm = 0F;
                    viewHolder.target.setText("--.- ºC");
                }
                break;
            case MASH:
                configuration.temp_sensor = "Mash In Temp Sensor";
                configuration.heat_actuator = "HLT Heating Actuator";
                if (viewHolder.target.getText().toString().contains("%")) {
                    configuration.phase.temperature = 0F;
                    configuration.phase.heat_pwm = 0F;
                    viewHolder.target.setText("--.- ºC");
                }
                break;
            case BOIL:
                configuration.temp_sensor = "Boil Out Temp Sensor";
                configuration.heat_actuator = "Boil Heating Actuator";
                if (viewHolder.target.getText().toString().contains("ºC")) {
                    configuration.phase.temperature = 0F;
                    configuration.phase.heat_pwm = 0.0F;
                    viewHolder.target.setText("0.0%");
                }
                break;
        }

        final ConfigurationBrewOperationActivity callback = this;
        progress.show();
        ConfigurationRequest.update(configuration.clone(), callback);

        updateActiveTempSensorCard();
    }

    private void adjustPumpSettings(SeekBar pump) {
        switch (pump.getId()) {
            case R.id.configuration_water_pump_seekbar:
                configuration.phase.pump_1_pwm = new Float(pump.getProgress());
                break;
            case R.id.configuration_wort_pump_seekbar:
                configuration.phase.pump_2_pwm = new Float(pump.getProgress());
                break;
        }

        final ConfigurationBrewOperationActivity callback = this;
        progress.show();
        ConfigurationRequest.update(configuration.clone(), callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_configuration_brew_operation, menu);
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
            case R.id.action_last_15_minutes:
                limit = 15;
                fetchLogData();
                result = true;
                break;
            case R.id.action_last_30_minutes:
                limit = 30;
                fetchLogData();
                result = true;
                break;
            case R.id.action_last_1_hour:
                limit = 60;
                fetchLogData();
                result = true;
                break;
            case R.id.action_last_2_hours:
                limit = 120;
                fetchLogData();
                result = true;
                break;
            case R.id.action_last_6_hours:
                limit = 360;
                fetchLogData();
                result = true;
                break;
            case R.id.action_last_9_hours:
                limit = 540;
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
}
