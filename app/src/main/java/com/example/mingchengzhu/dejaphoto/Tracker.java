package com.example.mingchengzhu.dejaphoto;
import android.location.Location;

/**
 * Created by tianhuic on 4/30/17.
 */

public class Tracker {

    private long time; //last time
    //private double longitude;
    //private double lattitude;
    private Location lastLocation;

    private boolean compareLocation(Location newLocation){
        float distance = lastLocation.distanceTo(newLocation); //in meters
        return distance > 153; //153 meters approximately equal to 500 feet
    }

    //private boolean compareLocation(double longitude, double lattitude){
    //}

    private boolean compareTime(){
        long currTime = System.currentTimeMillis(); //in millisecond
        return (currTime - time) >= 300000; // 5 minutes
    }

    public void update(){
        this.time = System.currentTimeMillis();
    }

    public boolean compare(Location location){
        return compare(location) || compareTime();
    }

/*
    public boolean compare(double longitude, double lattitude){
        return compareLocation(longitude, lattitude) || compareTime();
    } */
}
