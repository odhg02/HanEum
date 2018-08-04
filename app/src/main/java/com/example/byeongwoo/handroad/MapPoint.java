package com.example.byeongwoo.handroad;

/**
 * Created by odjh0 on 2018-07-26.
 */

public class MapPoint {
    private String Name;
    private double latitude;
    private double longitude;

    public MapPoint(){
        super();
    }
    public MapPoint(String Name, double latitude, double longitude){
        this.Name = Name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName(){
        return Name;
    }
    public float getLatitude(){
        return getLatitude();
    }
    public float getLongitude(){
        return getLongitude();
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
}
