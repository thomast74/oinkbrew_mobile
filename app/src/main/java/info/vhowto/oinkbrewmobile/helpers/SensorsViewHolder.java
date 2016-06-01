package info.vhowto.oinkbrewmobile.helpers;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Device;

public class SensorsViewHolder {
    private LinearLayout fridge_inside_layout;
    private LinearLayout fridge_outside_layout;
    private LinearLayout beer_1_layout;
    private LinearLayout beer_2_layout;
    private LinearLayout hlt_out_layout;
    private LinearLayout mash_in_layout;
    private LinearLayout mash_out_layout;
    private LinearLayout boil_inside_layout;
    private LinearLayout boil_out_layout;

    private Spinner fridge_inside;
    private Spinner fridge_outside;
    private Spinner beer_1;
    private Spinner beer_2;
    private Spinner hlt_out;
    private Spinner mash_in;
    private Spinner mash_out;
    private Spinner boil_inside;
    private Spinner boil_out;

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

    public void setSensors(Activity activity, ArrayList<Device> items) {
        Device pleaseSelect = new Device();
        pleaseSelect.name = "- Please Select -";
        items.add(0, pleaseSelect);

        Device[] devices = items.toArray(new Device[items.size()]);
        ArrayAdapter<Device> fridgeInsideAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> fridgeOutsideAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> beer1Adapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> beer2Adapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> hltOutAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> mashInAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> mashOutAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> boildInsideAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);
        ArrayAdapter<Device> boilOutAdapter = new ArrayAdapter<>(activity, R.layout.spinner_drop_down_item, devices);

        fridge_inside.setAdapter(fridgeInsideAdapter);
        fridge_outside.setAdapter(fridgeOutsideAdapter);
        beer_1.setAdapter(beer1Adapter);
        beer_2.setAdapter(beer2Adapter);
        hlt_out.setAdapter(hltOutAdapter);
        mash_in.setAdapter(mashInAdapter);
        mash_out.setAdapter(mashOutAdapter);
        boil_inside.setAdapter(boildInsideAdapter);
        boil_out.setAdapter(boilOutAdapter);
    }

    public Device getFridgeOutside() {
        return (Device)fridge_outside.getSelectedItem();
    }

    public Device getFridgeInside() {
        return (Device)fridge_inside.getSelectedItem();
    }

    public Device getBeer1() {
        return (Device)beer_1.getSelectedItem();
    }

    public Device getBeer2() {
        return (Device)beer_2.getSelectedItem();
    }
}
