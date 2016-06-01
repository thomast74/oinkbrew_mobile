package info.vhowto.oinkbrewmobile.helpers;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Device;

public class ActuatorsViewHolder {

    private LinearLayout cooling_layout;
    private LinearLayout heating_1_layout;
    private LinearLayout heating_2_layout;
    private LinearLayout fan_layout;
    private LinearLayout pump_1_layout;
    private LinearLayout pump_2_layout;
    private TextView lbl_heating_1;
    private TextView lbl_heating_2;
    private Spinner cooling;
    private Spinner heating_1;
    private Spinner heating_2;
    private Spinner fan;
    private Spinner pump_1;
    private Spinner pump_2;

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

        lbl_heating_1.setText(R.string.configuration_new_heating_fridge);
    }

    public void changeToBrew() {
        cooling_layout.setVisibility(View.GONE);
        heating_1_layout.setVisibility(View.VISIBLE);
        heating_2_layout.setVisibility(View.VISIBLE);
        fan_layout.setVisibility(View.GONE);
        pump_1_layout.setVisibility(View.VISIBLE);
        pump_2_layout.setVisibility(View.VISIBLE);

        lbl_heating_1.setText(R.string.configuration_new_heating_hlt);
        lbl_heating_2.setText(R.string.configuration_new_heating_boil);
    }

    public void setActuators(Activity activity, ArrayList<Device> items) {
        Device pleaseSelect = new Device();
        pleaseSelect.name = "- Please Select -";
        items.add(0, pleaseSelect);

        Device[] devices = items.toArray(new Device[items.size()]);
        ArrayAdapter<Device> coolingAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> heating1Adapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> heating2Adapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> fanAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> pump1Adapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> pump2Adapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);

        cooling.setAdapter(coolingAdapter);
        heating_1.setAdapter(heating1Adapter);
        heating_2.setAdapter(heating2Adapter);
        fan.setAdapter(fanAdapter);
        pump_1.setAdapter(pump1Adapter);
        pump_2.setAdapter(pump2Adapter);
    }

    public Device getCooling() {
        return (Device)cooling.getSelectedItem();
    }
    public Device getFridgeHeating() {
        return (Device)heating_1.getSelectedItem();
    }
    public Device getFan() { return (Device)fan.getSelectedItem(); }
    public Device getHltHeating() {
        return (Device)heating_1.getSelectedItem();
    }
    public Device getBoilHeating() {
        return (Device)heating_2.getSelectedItem();
    }
    public Device getPump1() {
        return (Device)pump_1.getSelectedItem();
    }
    public Device getPump2() {
        return (Device)pump_2.getSelectedItem();
    }

}
