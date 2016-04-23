package info.vhowto.oinkbrewmobile.domain;

import java.util.Date;

public class BrewPi {

    public BrewPi() {
    }

    public BrewPi(String device_id, String name, String firmware_version, String system_version, String spark_version) {
        this.device_id = device_id;
        this.name = name;
        this.firmware_version = firmware_version;
        this.system_version = system_version;
        this.spark_version = spark_version;
    }

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
}
