package info.vhowto.oinkbrewmobile.domain;

import com.google.gson.Gson;

import java.util.Date;

public class Device {

    public int pk;
    public String name;
    public int function;
    public int device_type;
    public int configuration;
    public boolean is_deactivate;

    public BrewPi brewPi = new BrewPi();
    public int pin_nr;
    public String hw_address;

    public Date last_update;
    public float value;

    public float offset;
    public String offset_result;
    public float offset_from_brewpi;

    public Device() {

    }

    public static Device fromJson(String json) {
        return new Gson().fromJson(json, Device.class);
    }

    public boolean isActuator() { return device_type == 1; }

    public boolean isTempSensor() {
        return device_type == 3;
    }

    public boolean isInUse() { return  function > 0; }

    public String toString() {
        if (name == null || name.isEmpty()) {
            return String.format("%d/%s", pin_nr, hw_address);
        }
        else {
            return name;
        }
    }
}
