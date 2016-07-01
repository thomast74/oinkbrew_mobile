package info.vhowto.oinkbrewmobile.domain;

import com.google.gson.Gson;

import java.util.Date;

public class Log {

    public Date create_date;
    public String name;
    public int pk;
    public String type;
    public String device_id;

    public LogPoint[] points = new LogPoint[] {};


    public Log() {
    }

    public static Log fromJson(String json) {
        return new Gson().fromJson(json, Log.class);
    }
}
