package com.example.mingchengzhu.dejaphoto;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.Service;
import android.content.Context;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


/**
 * Created by tianhuic on 4/30/17.
 */

public class Tracker{

    private long time; //last time
    private Location lastLocation;

 //   private double latitude;
//    private double longitude;

    public Location getLocation(){
        return lastLocation;
    }

    /* For testing */
    public void set(long time, Location location){
        this.time = time;
    }

    /*
    public boolean compareTime(){
        long currTime = System.currentTimeMillis(); //in millisecond
        return (currTime - time) >= 300000; // 5 minutes
    }
*/
    public void updateTime(){
        this.time = System.currentTimeMillis();
    }

    public void updateLocation(Location location){
        if (location != null) {
            lastLocation = location;
   //         latitude = location.getLatitude();
     //       longitude = location.getLongitude();
        }
    }

    //public boolean compare(Location location){
    //    return compare(location) || compareTime();
    //}

}
