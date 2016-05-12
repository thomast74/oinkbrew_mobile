package info.vhowto.oinkbrewmobile.domain;

import com.google.gson.Gson;

import java.util.Date;

public class Log {

    public Date create_date;
    public String name;
    public int pk;
    public String type;
    public String device_id;

    public FermentationPoint[] points = new FermentationPoint[] {};


    public Log() {
    }

    public static Log fromJson(String json) {
        return new Gson().fromJson(json, Log.class);
    }
}
