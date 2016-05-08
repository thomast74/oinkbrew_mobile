package info.vhowto.oinkbrewmobile.domain;

import java.io.Serializable;
import java.util.Date;

public class Phase implements Serializable {

    public Date start_date;

    public Float temperature;

    public Float heat_pwm;
    public Float pump_1_pwm;
    public Float pump_2_pwm;
    public Float fan_pwm;

    public Long heating_period;
    public Long cooling_period;
    public Long cooling_on_time;
    public Long cooling_off_time;

    public Float p;
    public Float i;
    public Float d;

    public Boolean done;
}
