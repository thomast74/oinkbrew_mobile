package info.vhowto.oinkbrewmobile.domain;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;

public class BrewPi implements Serializable {

    public String device_id;
    public String name;

    public String firmware_version;
    public String system_version;
    public String spark_version;

    public String ip_address;
    public String web_address;
    public String web_port;

    public Long brewpi_time;

    public Date last_update;

    public BrewPi() {
    }

    public static BrewPi fromJson(String json) {
        return new Gson().fromJson(json, BrewPi.class);
    }

    public static String toJson(BrewPi brewpi) {
        return new Gson().toJson(brewpi);
    }

    public BrewPi clone() {
        BrewPi clone = new BrewPi();

        clone.device_id = device_id;
        clone.name = name;
        clone.firmware_version = firmware_version;
        clone.system_version = system_version;
        clone.spark_version = spark_version;
        clone.ip_address = ip_address;
        clone.web_address = web_address;
        clone.web_port = web_port;
        clone.brewpi_time = brewpi_time;
        clone.last_update = last_update;

        return clone;
    }

}
