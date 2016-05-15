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

    public HashMap<String, Integer> function = new HashMap<String, Integer>() {};
    public Phase[] phases = new Phase[] {};
    public Phase phase = new Phase();

    public Boolean archived;


    public Configuration() {
    }

    public static Configuration fromJson(String json) {
        return new Gson().fromJson(json, Configuration.class);
    }

    public static String toJson(Configuration configuration) {
        return new Gson().toJson(configuration);
    }

    public Configuration clone() {
        Configuration clone = new Configuration();

        clone.pk = pk;
        clone.name = name;
        clone.create_date = create_date;
        clone.type = type;
        clone.brewpi = brewpi.clone();

        clone.heat_actuator = heat_actuator;
        clone.cool_actuator = cool_actuator;
        clone.fan_actuator = fan_actuator;
        clone.pump_1_actuator = pump_1_actuator;
        clone.pump_2_actuator = pump_2_actuator;
        clone.temp_sensor = temp_sensor;

        clone.archived = archived;

        clone.function = new HashMap<>();
        for (Map.Entry<String, Integer> entry : function.entrySet()) {
            clone.function.put(entry.getKey(), entry.getValue());
        }

        clone.phase = phase.clone();

        return clone;
    }
}
