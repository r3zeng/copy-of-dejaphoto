package com.example.mingchengzhu.dejaphoto;
import android.location.Location;

/**
 * Created by tianhuic on 4/30/17.
 */

public class Tracker {

    private long time; //last time
    private Location lastLocation;

    /* For testing */
    public void set(long time, Location location){
        this.time = time;
    }

    private boolean compareLocation(Location newLocation){
        float distance = lastLocation.distanceTo(newLocation); //in meters
        return distance > 153; //153 meters approximately equal to 500 feet
    }

    public boolean compareTime(){
        long currTime = System.currentTimeMillis(); //in millisecond
        return (currTime - time) >= 300000; // 5 minutes
    }

    public void update(){
        this.time = System.currentTimeMillis();
    }

    public boolean compare(Location location){
        return compare(location) || compareTime();
    }

}
