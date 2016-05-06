package info.vhowto.oinkbrewmobile.domain;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Configuration implements Serializable {

    public int pk;
    public String name;
    public Date create_date;
    public String type;

    public BrewPi brewpi = new BrewPi();

    public String heat_actuator;
    public String cool_actuator;
    public String fan_actuator;
    public String pump_1_actuator;
    public String pump_2_actuator;
    public String temp_sensor;

    Map<String, Integer> function = new HashMap<String, Integer>() {};
    Phase[] phases = new Phase[] {};

    public Boolean archived;


    public Configuration() {
    }

    public static Configuration fromJson(String json) {
        return new Gson().fromJson(json, Configuration.class);
    }

    public static String toJson(Configuration configuration) {
        return new Gson().toJson(configuration);
    }
}
