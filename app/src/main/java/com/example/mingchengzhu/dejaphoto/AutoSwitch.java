package com.example.mingchengzhu.dejaphoto;

import android.os.Handler;
import android.util.Log;

/**
 * Created by Tianhui Cao, Mingcheng Zhu on 5/10/17.
 * Description: implements auto-switch background
 */
public class AutoSwitch implements Runnable {
    private static final String TAG = "AutoSwitch";

    Handler handler;
    PhotoManagerClient client;
    PhotoManager manager;
    private int refresh_time;


    public AutoSwitch(PhotoManagerClient client, PhotoManager manager, Handler handler, int refresh_time){
        this.handler = handler;
        this.client = client;
        this.manager = manager;
        this.refresh_time = refresh_time;
    }

    @Override
    public void run() {
        Log.i(TAG, "Auto switch timer firing now");

        //call the change background method here
        manager.next();

        handler.postDelayed(this, refresh_time);//3 minutes 180000
    }

    public int getTime(){return refresh_time;}

    public void setTime(int time){
        refresh_time = time;
    }

    public void refresh(){
        Log.i(TAG, "The timer is refresed");
        if (this != null) {
            handler.removeCallbacks(this);
            handler.postDelayed(this, refresh_time);
        }
    }
}
