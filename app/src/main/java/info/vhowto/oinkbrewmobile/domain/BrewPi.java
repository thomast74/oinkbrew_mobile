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

}
