package com.example.mingchengzhu.dejaphoto;

import android.os.Handler;

/**
 * Created by Tianhui Cao, Mingcheng Zhu on 5/10/17.
 * Description: implements auto-switch background
 */

public class AutoSwitch implements Runnable {
    Handler handler;
    MainActivity activity;
    private int refresh_time;


    AutoSwitch(MainActivity activity, Handler handler, int refresh_time){
        this.handler = handler;
        this.activity = activity;
        this.refresh_time = refresh_time;
    }

    @Override
    public void run() {
        //call the change background method here
        PhotoManager algo = new PhotoManager(activity);
        algo.next();

        handler.postDelayed(this, refresh_time);//3 minutes 180000
    }

    public void setTime(int time){
        refresh_time = time;
    }

    public void refresh(){
        if(this != null){
            handler.removeCallbacks(this);
            handler.postDelayed(this, refresh_time);
        }
    }
}
