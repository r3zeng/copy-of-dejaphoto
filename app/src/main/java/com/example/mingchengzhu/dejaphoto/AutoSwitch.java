package com.example.mingchengzhu.dejaphoto;

import android.os.Handler;

// For testing: To be deleted
import java.util.Random;
import android.widget.TextView;

/**
 * Created by Tianhui Cao, Mingcheng Zhu on 5/10/17.
 * Description: implements auto-switch background
 */

public class AutoSwitch implements Runnable {
    Handler handler;
    MainActivity activity;
    private int refresh_time;

    // For testing
    TextView textView;
    TextView textView2;

    AutoSwitch(MainActivity activity, Handler handler, int refresh_time, TextView textView, TextView textView2){
        this.handler = handler;
        this.activity = activity;
        this.refresh_time = refresh_time;

        this.textView = textView;
        this.textView2 = textView2;
    }

    @Override
    public void run() {
        //call the change background method here
        activity.SwipeRight();

        //for testing
        Random random = new Random();
        textView.setText(String.valueOf(random.nextInt(50)+1));
        textView2.setText(String.valueOf(random.nextInt(50)+1));

        handler.postDelayed(this, refresh_time);//3 minutes 180000
    }
}
