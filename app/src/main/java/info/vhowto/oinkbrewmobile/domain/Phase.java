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
        start_date = new Date();
        temperature = 0.0F;
        heat_pwm = 0.0F;
        pump_1_pwm = 0.0F;
        pump_2_pwm = 0.0F;
        fan_pwm = 0.0F;
        heating_period = 0L;
        cooling_period = 0L;
        cooling_on_time = 0L;
        cooling_off_time = 0L;
        p = 0.0F;
        i = 0.0F;
        d = 0.0F;
        done = false;
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
