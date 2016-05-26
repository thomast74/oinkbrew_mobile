package info.vhowto.oinkbrewmobile.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.BrewPi;
import info.vhowto.oinkbrewmobile.domain.ConfigurationType;
import info.vhowto.oinkbrewmobile.domain.Device;
import info.vhowto.oinkbrewmobile.remote.BrewPiRequest;
import info.vhowto.oinkbrewmobile.remote.DeviceRequest;
import info.vhowto.oinkbrewmobile.remote.RequestArrayCallback;


public class ConfigurationNewActivity extends AppCompatActivity {

    private ActuatorsViewHolder actuatorsViewHolder;
    private SensorsViewHolder sensorsViewHolder;
    private Spinner type;
    private Spinner brewpi;
    private ArrayList<Device> devices;


    private class ActuatorsViewHolder {
        LinearLayout cooling_layout;
        LinearLayout heating_1_layout;
        LinearLayout heating_2_layout;
        LinearLayout fan_layout;
        LinearLayout pump_1_layout;
        LinearLayout pump_2_layout;
        TextView lbl_heating_1;
        TextView lbl_heating_2;
        Spinner cooling;
        Spinner heating_1;
        Spinner heating_2;
        Spinner fan;
        Spinner pump_1;
        Spinner pump_2;

        public ActuatorsViewHolder(Activity activity) {
            cooling_layout = (LinearLayout)activity.findViewById(R.id.cooling_actuator_parent);
            heating_1_layout = (LinearLayout)activity.findViewById(R.id.heating_1_actuator_parent);
            heating_2_layout = (LinearLayout)activity.findViewById(R.id.heating_2_actuator_parent);
            fan_layout = (LinearLayout)activity.findViewById(R.id.fan_actuator_parent);
            pump_1_layout = (LinearLayout)activity.findViewById(R.id.pump_1_actuator_parent);
            pump_2_layout = (LinearLayout)activity.findViewById(R.id.pump_2_actuator_parent);

            lbl_heating_1 = (TextView)activity.findViewById(R.id.lbl_heating_1_actuator);
            lbl_heating_2 = (TextView)activity.findViewById(R.id.lbl_heating_2_actuator);

            cooling = (Spinner)activity.findViewById(R.id.cooling_actuator);
            heating_1 = (Spinner)activity.findViewById(R.id.heating_1_actuator);
            heating_2 = (Spinner)activity.findViewById(R.id.heating_2_actuator);
            fan = (Spinner)activity.findViewById(R.id.fan_actuator);
            pump_1 = (Spinner)activity.findViewById(R.id.pump_1_actuator);
            pump_2 = (Spinner)activity.findViewById(R.id.pump_2_actuator);
        }

        public void changeToNoSelect() {
            cooling_layout.setVisibility(View.GONE);
            heating_1_layout.setVisibility(View.GONE);
            heating_2_layout.setVisibility(View.GONE);
            fan_layout.setVisibility(View.GONE);
            pump_1_layout.setVisibility(View.GONE);
            pump_2_layout.setVisibility(View.GONE);
        }

        public void changeToFermentation() {
            cooling_layout.setVisibility(View.VISIBLE);
            heating_1_layout.setVisibility(View.VISIBLE);
            heating_2_layout.setVisibility(View.GONE);
            fan_layout.setVisibility(View.VISIBLE);
            pump_1_layout.setVisibility(View.GONE);
            pump_2_layout.setVisibility(View.GONE);

            lbl_heating_1.setText("Heating");
        }

        public void changeToBrew() {
            cooling_layout.setVisibility(View.GONE);
            heating_1_layout.setVisibility(View.VISIBLE);
            heating_2_layout.setVisibility(View.VISIBLE);
            fan_layout.setVisibility(View.GONE);
            pump_1_layout.setVisibility(View.VISIBLE);
            pump_2_layout.setVisibility(View.VISIBLE);

            lbl_heating_1.setText("Heating HLT");
            lbl_heating_2.setText("Heating Boil");
        }
    }

    private class SensorsViewHolder {
        LinearLayout fridge_inside_layout;
        LinearLayout fridge_outside_layout;
        LinearLayout beer_1_layout;
        LinearLayout beer_2_layout;
        LinearLayout hlt_out_layout;
        LinearLayout mash_in_layout;
        LinearLayout mash_out_layout;
        LinearLayout boil_inside_layout;
        LinearLayout boil_out_layout;

        Spinner fridge_inside;
        Spinner fridge_outside;
        Spinner beer_1;
        Spinner beer_2;
        Spinner hlt_out;
        Spinner mash_in;
        Spinner mash_out;
        Spinner boil_inside;
        Spinner boil_out;

        public SensorsViewHolder(Activity activity) {
            fridge_inside_layout = (LinearLayout)activity.findViewById(R.id.fridge_inside_sensor_parent);
            fridge_outside_layout = (LinearLayout)activity.findViewById(R.id.fridge_outside_sensor_parent);
            beer_1_layout = (LinearLayout)activity.findViewById(R.id.beer_1_sensor_parent);
            beer_2_layout = (LinearLayout)activity.findViewById(R.id.beer_2_sensor_parent);
            hlt_out_layout = (LinearLayout)activity.findViewById(R.id.hlt_out_sensor_parent);
            mash_in_layout = (LinearLayout)activity.findViewById(R.id.mash_in_sensor_parent);
            mash_out_layout = (LinearLayout)activity.findViewById(R.id.mash_out_sensor_parent);
            boil_inside_layout = (LinearLayout)activity.findViewById(R.id.boil_inside_sensor_parent);
            boil_out_layout = (LinearLayout)activity.findViewById(R.id.boil_out_sensor_parent);

            fridge_inside = (Spinner)activity.findViewById(R.id.fridge_inside_sensor);
            fridge_outside = (Spinner)activity.findViewById(R.id.fridge_outside_sensor);
            beer_1 = (Spinner)activity.findViewById(R.id.beer_1_sensor);
            beer_2 = (Spinner)activity.findViewById(R.id.beer_2_sensor);
            hlt_out = (Spinner)activity.findViewById(R.id.hlt_out_sensor);
            mash_in = (Spinner)activity.findViewById(R.id.mash_in_sensor);
            mash_out = (Spinner)activity.findViewById(R.id.mash_out_sensor);
            boil_inside = (Spinner)activity.findViewById(R.id.boil_inside_sensor);
            boil_out = (Spinner)activity.findViewById(R.id.boil_out_sensor);
        }

        public void changeToNoSelect() {
            fridge_inside_layout.setVisibility(View.GONE);
            fridge_outside_layout.setVisibility(View.GONE);
            beer_1_layout.setVisibility(View.GONE);
            beer_2_layout.setVisibility(View.GONE);
            hlt_out_layout.setVisibility(View.GONE);
            mash_in_layout.setVisibility(View.GONE);
            mash_out_layout.setVisibility(View.GONE);
            boil_inside_layout.setVisibility(View.GONE);
            boil_out_layout.setVisibility(View.GONE);
        }

        public void changeToFermentation() {
            fridge_inside_layout.setVisibility(View.VISIBLE);
            fridge_outside_layout.setVisibility(View.VISIBLE);
            beer_1_layout.setVisibility(View.VISIBLE);
            beer_2_layout.setVisibility(View.VISIBLE);
            hlt_out_layout.setVisibility(View.GONE);
            mash_in_layout.setVisibility(View.GONE);
            mash_out_layout.setVisibility(View.GONE);
            boil_inside_layout.setVisibility(View.GONE);
            boil_out_layout.setVisibility(View.GONE);
        }

        public void changeToBrew() {
            fridge_inside_layout.setVisibility(View.GONE);
            fridge_outside_layout.setVisibility(View.GONE);
            beer_1_layout.setVisibility(View.GONE);
            beer_2_layout.setVisibility(View.GONE);
            hlt_out_layout.setVisibility(View.VISIBLE);
            mash_in_layout.setVisibility(View.VISIBLE);
            mash_out_layout.setVisibility(View.VISIBLE);
            boil_inside_layout.setVisibility(View.VISIBLE);
            boil_out_layout.setVisibility(View.VISIBLE);
        }

        public void clearSensors() {

        }

        public void setSensors(ArrayList<Device> items) {
            ArrayAdapter<Device> adapter = new ArrayAdapter<Device>(this, R.layout.spinner_drop_down_item, items.toArray());
            type.setAdapter(adapter);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_new);

        final ConfigurationNewActivity activity = this;

        type = (Spinner)findViewById(R.id.type);
        brewpi = (Spinner)findViewById(R.id.brewpi);
        actuatorsViewHolder = new ActuatorsViewHolder(this);
        actuatorsViewHolder.changeToNoSelect();
        sensorsViewHolder = new SensorsViewHolder(this);
        sensorsViewHolder.changeToNoSelect();

        prepareTypeSpinner();
        prepareBrewPiSpinner();

        ((Button)findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfiguration();
            }
        });

        ((Button)findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    private void prepareTypeSpinner() {
        String[] typeValues = new String[] { "- Please Select -", ConfigurationType.BREW, ConfigurationType.FERMENTATION };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_drop_down_item, typeValues);
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

                ArrayAdapter<BrewPi> adapter = new ArrayAdapter<BrewPi>(activity, R.layout.spinner_drop_down_item, items);
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

        if (selectedType.equals("- Please Select -") || devices == null || devices.isEmpty())
            return;

        // activate drop downs based on type
        // clear these drop downs

        // fill drop downs with actuators
        // drop downs need to be configured that if selected this entry needs to be removed
        // from the other actuator drop downs
    }

    private void prepareSensors(ArrayList<Device> items) {
        String selectedType = (String)type.getSelectedItem();

        if (selectedType.equals("- Please Select -") || devices == null || devices.isEmpty())
            return;

        // activate drop downs based on type
        // clear these drop downs

        // fill drop downs with sensors
        // drop downs need to be configured that if selected this entry needs to be removed
        // from the other sensor drop downs

    }

    private void saveConfiguration() {

    }
}
