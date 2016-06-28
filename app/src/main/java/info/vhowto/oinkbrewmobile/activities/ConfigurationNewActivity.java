package info.vhowto.oinkbrewmobile.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.BrewPi;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.domain.ConfigurationType;
import info.vhowto.oinkbrewmobile.domain.Device;
import info.vhowto.oinkbrewmobile.domain.Phase;
import info.vhowto.oinkbrewmobile.helpers.ActuatorsViewHolder;
import info.vhowto.oinkbrewmobile.helpers.SensorsViewHolder;
import info.vhowto.oinkbrewmobile.remote.BrewPiRequest;
import info.vhowto.oinkbrewmobile.remote.ConfigurationRequest;
import info.vhowto.oinkbrewmobile.remote.DeviceRequest;
import info.vhowto.oinkbrewmobile.remote.RequestArrayCallback;
import info.vhowto.oinkbrewmobile.remote.RequestObjectCallback;


public class ConfigurationNewActivity extends AppCompatActivity {

    private ActuatorsViewHolder actuatorsViewHolder;
    private SensorsViewHolder sensorsViewHolder;
    private TextView name;
    private Spinner type;
    private Spinner brewpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_new);

        final ConfigurationNewActivity activity = this;

        name = (TextView)findViewById(R.id.name);
        type = (Spinner)findViewById(R.id.type);
        brewpi = (Spinner)findViewById(R.id.brewpi);

        actuatorsViewHolder = new ActuatorsViewHolder(this);
        actuatorsViewHolder.changeToNoSelect();
        sensorsViewHolder = new SensorsViewHolder(this);
        sensorsViewHolder.changeToNoSelect();

        prepareTypeSpinner();
        prepareBrewPiSpinner();

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfiguration();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    private void prepareTypeSpinner() {
        String[] typeValues = new String[] { "- Please Select -", ConfigurationType.BREW, ConfigurationType.FERMENTATION };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_drop_down_item, typeValues);
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String typeSelected = (String)type.getSelectedItem();
                if (ConfigurationType.BREW.equals(typeSelected)) {
                    actuatorsViewHolder.changeToBrew();
                    sensorsViewHolder.changeToBrew();
                }
                else if (ConfigurationType.FERMENTATION.equals(typeSelected)) {
                    actuatorsViewHolder.changeToFermentation();
                    sensorsViewHolder.changeToFermentation();
                }
                else {
                    actuatorsViewHolder.changeToNoSelect();
                    sensorsViewHolder.changeToNoSelect();
                }
            }

            public void onNothingSelected(AdapterView<?> parent )
            {
                actuatorsViewHolder.changeToNoSelect();
                sensorsViewHolder.changeToNoSelect();
            }
        });

    }

    private void prepareBrewPiSpinner() {
        final ConfigurationNewActivity activity = this;
        final RequestArrayCallback callback = new RequestArrayCallback<BrewPi>() {
            @Override
            public void onRequestSuccessful() {
            }

            @Override
            public void onRequestSuccessful(ArrayList<BrewPi> items) {
                BrewPi pleaseSelect = new BrewPi();
                pleaseSelect.device_id = "";
                pleaseSelect.name = "- Please Select -";
                items.add(0, pleaseSelect);

                ArrayAdapter<BrewPi> adapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, items);
                brewpi.setAdapter(adapter);
            }

            @Override
            public void onRequestFailure(int statusCode, String errorMessage) {
                switch (statusCode) {
                    case 404:
                        Toast.makeText(getApplicationContext(), getString(R.string.error_brewpis_empty), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Context getApplicationContext() {
                return activity.getApplicationContext();
            }
        };
        brewpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BrewPi item = (BrewPi)brewpi.getSelectedItem();
                if (item != null && !item.device_id.equals("")) {
                    loadDevices(item);
                }
            }

            public void onNothingSelected(AdapterView<?> parent )
            {
            }
        });
        BrewPiRequest.getBrewPis(callback);
    }

    private void loadDevices(BrewPi item) {
        final ConfigurationNewActivity activity = this;

        RequestArrayCallback<Device> callback = new RequestArrayCallback<Device>() {
            @Override
            public void onRequestSuccessful() {

            }

            @Override
            public void onRequestSuccessful(ArrayList<Device> items) {
                prepareActuators(items);
                prepareSensors(items);
            }

            @Override
            public void onRequestFailure(int statusCode, String errorMessage) {
                switch (statusCode) {
                    case 404:
                        Toast.makeText(getApplicationContext(), getString(R.string.error_brewpis_empty), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Context getApplicationContext() {
                return activity.getApplicationContext();
            }
        };

        DeviceRequest.getDevices(item.device_id, callback);
    }

    private void prepareActuators(ArrayList<Device> items) {
        String selectedType = (String)type.getSelectedItem();

        if (selectedType.equals("- Please Select -") || items == null || items.isEmpty())
            return;

        ArrayList<Device> actuators = new ArrayList<>();
        for (Device device : items) {
            if (device.isActuator() && !device.isInUse()) {
                actuators.add(device);
            }
        }

        actuatorsViewHolder.setActuators(this, actuators);
    }

    private void prepareSensors(ArrayList<Device> items) {
        String selectedType = (String)type.getSelectedItem();

        if (selectedType.equals("- Please Select -") || items == null || items.isEmpty())
            return;

        ArrayList<Device> sensors = new ArrayList<>();
        for (Device device : items) {
            if (device.isTempSensor() && !device.isInUse()) {
                sensors.add(device);
            }
        }

        sensorsViewHolder.setSensors(this, sensors);
    }

    private void saveConfiguration() {

        if (name.getText().length() == 0) {
            showErrorMessage("You need to enter a name");
            return;
        }

        if (type.getSelectedItemPosition() == 0) {
            showErrorMessage("You need to select a configuration type");
            return;
        }

        if (brewpi.getSelectedItemPosition() == 0) {
            showErrorMessage("You need to select a BrewPi that will run the configuration");
            return;
        }

        Configuration configuration = null;
        final String typeSelected = (String)type.getSelectedItem();
        final ConfigurationNewActivity activity = this;
        if (ConfigurationType.FERMENTATION.equals(typeSelected)) {

            Device cooling = actuatorsViewHolder.getCooling();
            Device heating = actuatorsViewHolder.getFridgeHeating();
            Device fan = actuatorsViewHolder.getFan();

            Device fridgeOutside = sensorsViewHolder.getFridgeOutside();
            Device fridgeInside = sensorsViewHolder.getFridgeInside();
            Device beer1 = sensorsViewHolder.getBeer1();
            Device beer2 = sensorsViewHolder.getBeer2();

            if (cooling.pk == 0) {
                showErrorMessage("For a fermentation configuration you need to select a cooling actuator");
                return;
            }
            if (heating.pk == 0) {
                showErrorMessage("For a fermentation configuration you need to select a heating actuator");
                return;
            }
            if (cooling.pk == heating.pk || cooling.pk == fan.pk || heating.pk == fan.pk) {
                showErrorMessage("All actuators must be different");
                return;
            }
            if (fridgeInside.pk == 0) {
                showErrorMessage("For a fermentation configuration you need to select a fridge inside sensor");
                return;
            }
            if (beer1.pk == 0) {
                showErrorMessage("For a fermentation configuration you need to select a beer sensor");
                return;
            }
            if (fridgeOutside.pk > 0 && (fridgeOutside.pk == fridgeInside.pk || fridgeOutside.pk == beer1.pk || fridgeOutside.pk == beer2.pk) ||
                fridgeInside.pk == beer1.pk || fridgeInside.pk == beer2.pk || beer1.pk == beer2.pk) {
                showErrorMessage("All sensors must be different");

            }
            configuration = new Configuration();
            configuration.name = name.getText().toString();
            configuration.type = (String)type.getSelectedItem();
            configuration.cool_actuator = "Fridge Cooling Actuator";
            configuration.heat_actuator = "Fridge Heating Actuator";
            if (fan.pk > 0)
                configuration.fan_actuator = "Fridge Fan Actuator";
            configuration.temp_sensor = "Fridge Beer 1 Temp Sensor";

            configuration.function.put("Fridge Cooling Actuator", cooling.pk);
            configuration.function.put("Fridge Heating Actuator", heating.pk);
            configuration.function.put("Fridge Inside Temp Sensor", fridgeInside.pk);
            configuration.function.put("Fridge Beer 1 Temp Sensor", beer1.pk);

            if (fan.pk > 0)
                configuration.function.put("Fridge Fan Actuator", fan.pk);
            if (fridgeOutside.pk > 0)
                configuration.function.put("Outside Fridge Temp Sensor", fridgeOutside.pk);
            if (beer2.pk > 0)
                configuration.function.put("Fridge Beer 2 Temp Sensor", beer2.pk);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            configuration.phase = new Phase();
            configuration.phase.temperature = 19.0F;
            configuration.phase.fan_pwm = Float.parseFloat(prefs.getString("pref_fermentation_fan_pwm", "100.0"));
            configuration.phase.heating_period = Long.parseLong(prefs.getString("pref_fermentation_heat_period", "1000"));
            configuration.phase.cooling_period = Long.parseLong(prefs.getString("pref_fermentation_cooling_period", "600000"));
            configuration.phase.cooling_on_time = Long.parseLong(prefs.getString("pref_fermentation_cooling_on_time", "150000"));
            configuration.phase.cooling_off_time = Long.parseLong(prefs.getString("pref_fermentation_cooling_off_time", "180000"));
            configuration.phase.p = Float.parseFloat(prefs.getString("pref_fermentation_p", "18.0"));
            configuration.phase.i = Float.parseFloat(prefs.getString("pref_fermentation_i", "0.0001"));
            configuration.phase.d = Float.parseFloat(prefs.getString("pref_fermentation_d", "-8.0"));
        }
        else if (ConfigurationType.BREW.equals(typeSelected)) {

            Device hltHeating = actuatorsViewHolder.getHltHeating();
            Device boilHeating = actuatorsViewHolder.getBoilHeating();
            Device pump1 = actuatorsViewHolder.getPump1();
            Device pump2 = actuatorsViewHolder.getPump2();

            Device hltOut = sensorsViewHolder.getHltOut();
            Device mashIn = sensorsViewHolder.getMashIn();
            Device mashOut = sensorsViewHolder.getMashOut();
            //Device boilInside = sensorsViewHolder.getBoilInside();
            Device boilOut = sensorsViewHolder.getBoilOut();

            if (hltHeating.pk == 0) {
                showErrorMessage("For a brew configuration you need to select a hlt heating actuator");
                return;
            }
            if (boilHeating.pk == 0) {
                showErrorMessage("For a brew configuration you need to select a boil heating actuator");
                return;
            }
            if (pump1.pk == 0) {
                showErrorMessage("For a brew configuration you need to select a water pump actuator");
                return;
            }
            if (pump2.pk == 0) {
                showErrorMessage("For a brew configuration you need to select a wort pump actuator");
                return;
            }
            if (hltHeating.pk == boilHeating.pk || hltHeating.pk == pump1.pk || hltHeating.pk == pump2.pk ||
                boilHeating.pk == pump1.pk || boilHeating.pk == pump2.pk ||
                pump1.pk == pump2.pk) {
                showErrorMessage("All actuators must be different");
                return;
            }

            if (hltOut.pk == 0) {
                showErrorMessage("For a fermentation configuration you need to select a fridge inside sensor");
                return;
            }
            if (mashIn.pk == 0) {
                showErrorMessage("For a fermentation configuration you need to select a beer sensor");
                return;
            }
            if (mashOut.pk == 0) {
                showErrorMessage("For a fermentation configuration you need to select a beer sensor");
                return;
            }
            if (boilOut.pk == 0) {
                showErrorMessage("For a fermentation configuration you need to select a beer sensor");
                return;
            }
            if (hltOut.pk == mashIn.pk || hltOut.pk == mashOut.pk || hltOut.pk == boilOut.pk ||
                mashIn.pk == mashOut.pk || mashIn.pk == boilOut.pk ||
                mashOut.pk == boilOut.pk) {
                showErrorMessage("All sensors must be different");

            }
            configuration = new Configuration();
            configuration.name = name.getText().toString();
            configuration.type = (String)type.getSelectedItem();

            configuration.heat_actuator = "HLT Heating Actuator";
            configuration.pump_1_actuator = "Pump 1 Actuator";
            configuration.pump_2_actuator = "Pump 2 Actuator";
            configuration.temp_sensor = "HLT Out Temp Sensor";

            configuration.function.put("HLT Heating Actuator", hltHeating.pk);
            configuration.function.put("Boil Heating Actuator", boilHeating.pk);
            configuration.function.put("Pump 1 Actuator", pump1.pk);
            configuration.function.put("Pump 2 Actuator", pump2.pk);
            configuration.function.put("HLT Out Temp Sensor", hltOut.pk);
            configuration.function.put("Mash In Temp Sensor", mashIn.pk);
            configuration.function.put("Mash Out Temp Sensor", mashOut.pk);
            configuration.function.put("Boil Out Temp Sensor", boilOut.pk);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            configuration.phase = new Phase();
            configuration.phase.temperature = 1.0F;
            configuration.phase.pump_1_pwm = 100.0F;
            configuration.phase.pump_2_pwm = 100.0F;
            configuration.phase.heating_period = Long.parseLong(prefs.getString("pref_brew_heat_period", "2000"));
            configuration.phase.p = Float.parseFloat(prefs.getString("pref_brew_p", "90.0"));
            configuration.phase.i = Float.parseFloat(prefs.getString("pref_brew_i", "0.0001"));
            configuration.phase.d = Float.parseFloat(prefs.getString("pref_brew_d", "-45.0"));
        }

        if (configuration != null) {
            BrewPi selectedBrewPi = (BrewPi)brewpi.getSelectedItem();
            configuration.brewpi.device_id = selectedBrewPi.device_id;

            ConfigurationRequest.create(configuration, new RequestObjectCallback<Configuration>() {
                @Override
                public void onRequestSuccessful() {
                    // will not be called
                }

                @Override
                public void onRequestSuccessful(Configuration item) {
                    Intent intent = null;
                    if (ConfigurationType.FERMENTATION.equals(typeSelected)) {
                        intent = new Intent(activity, ConfigurationFermentationOperationActivity.class);

                    }
                    else if (ConfigurationType.BREW.equals(typeSelected)) {
                        intent = new Intent(activity, ConfigurationBrewOperationActivity.class);
                    }
                    if (intent != null) {
                        intent.putExtra("item", item);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }

                @Override
                public void onRequestFailure(int statusCode, String errorMessage) {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }

                @Override
                public Context getApplicationContext() {
                    return activity.getApplicationContext();
                }
            });
        }
    }

    private void showErrorMessage(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
