package info.vhowto.oinkbrewmobile.domain;

import java.util.Date;

public class LogPoint {

    public Date time;
    public float Target;

    // Fermentation
    public float Beer_1;
    public float Beer_2;
    public float Fridge;
    public float Heating;
    public float Cooling;

    // Brew
    public float Boil_Heating;
    public float HLT_Heating;
    public float Pump_1;
    public float Pump_2;
    public float HLT_Out;
    public float Mash_In;
    public float Mash_Out;
    public float Boil_Out;

    public LogPoint() {

    }

}
