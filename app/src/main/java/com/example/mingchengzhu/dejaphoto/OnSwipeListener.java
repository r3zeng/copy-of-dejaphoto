package com.example.mingchengzhu.dejaphoto;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created by Tianhui Cao, Mingcheng Zhu on 5/8/17.
 * Description: implements gesture recognition for the app
 * Reference: http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
 *            https://developer.android.com/training/gestures/detector.html
 */
public class OnSwipeListener implements OnTouchListener {

    private final GestureDetector gestureDetector;

    /**
     * Constructor
     * @param context - MainActivity.this
     */
    public OnSwipeListener(Context context){
        gestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event){
        return gestureDetector.onTouchEvent(event);
    }

    private class MyGestureListener extends SimpleOnGestureListener{

        private static final int THRESHOLD = 100; // threshold for swiping distance
        private static final int VELOCITY_THRESHOLD = 100; // threshold for swiping velocity

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            boolean result = false;
            try{
                float diffY = event2.getY() - event1.getY();
                float diffX = event2.getX() - event1.getX();
                // Horizontal swipe
                if (Math.abs(diffX) > Math.abs(diffY)){
                    if (Math.abs(diffX) > THRESHOLD && Math.abs(velocityX) > VELOCITY_THRESHOLD){
                        if (diffX > 0)
                            onSwipeLeft();
                        else
                            onSwipeRight();
                        result = true;
                    }
                }
                // Vertical swipe
                else if (Math.abs(diffY) > THRESHOLD && Math.abs(velocityY) > VELOCITY_THRESHOLD) {
                    if (diffY > 0)
                        onSwipeDown();
                    else
                        onSwipeTop();
                    result = true;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

    }

    /**
     *  Methods to be defined in MainActivity for functionality
     */
    public void onSwipeTop(){}
    public void onSwipeDown(){}
    public void onSwipeLeft(){}
    public void onSwipeRight(){}

}
