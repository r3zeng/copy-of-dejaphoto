package com.example.mingchengzhu.dejaphoto;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.Service;
import android.content.Context;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


/**
 * Created by Tianhui Cao, Mingcheng Zhu on 4/30/17.
 * Description: Getter class for most current system location and time
 */
public class Tracker {

    private long time; //system time
    private Location lastLocation; //system location

    /**
     * getter method for current system location
     * @return current system location
     */
    public Location getLocation() {
        return lastLocation;
    }

    /**
     * getter method for current system time
     * @return current system time
     */
    public long getTime(){
        updateTime();
        return time;
    }

    /**
     *  update the time to most current system time
     */
    public void updateTime() {
        this.time = System.currentTimeMillis();
    }

    /**
     * update the location to given location
     * @param location 
     */
    public void updateLocation(Location location) {
        if (location != null) {
            lastLocation = location;
        }
    }
}
