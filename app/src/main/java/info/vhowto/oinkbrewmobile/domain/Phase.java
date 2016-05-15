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

    public Phase() {
    }

    public Phase clone() {
        Phase clone = new Phase();

        clone.start_date = start_date;
        clone.temperature = temperature;
        clone.heat_pwm = heat_pwm;
        clone.pump_1_pwm = pump_1_pwm;
        clone.pump_2_pwm = pump_2_pwm;
        clone.fan_pwm = fan_pwm;
        clone.heating_period = heating_period;
        clone.cooling_period = cooling_period;
        clone.cooling_on_time = cooling_on_time;
        clone.cooling_off_time = cooling_off_time;
        clone.p = p;
        clone.i = i;
        clone.d = d;
        clone.done = done;

        return clone;
    }
}
