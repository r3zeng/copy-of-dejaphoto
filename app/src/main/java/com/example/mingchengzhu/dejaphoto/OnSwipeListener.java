package com.example.mingchengzhu.dejaphoto;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tianhuic on 5/8/17.
 * Reference: http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
 *            https://developer.android.com/training/gestures/detector.html
 */

public class OnSwipeListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeListener(Context context){
        gestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event){
        return gestureDetector.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{

        private final int threshold = 100;
        private final int velocity_threshold = 100;

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
                if (Math.abs(diffX) < Math.abs(diffY)){
                    if (Math.abs(diffY) > threshold && velocityY > velocity_threshold) {
                        if (diffY > 0)
                            onSwipeTop();
                        else
                            onSwipeDown();
                        result = true;
                    }
                }
                else{
                    if (Math.abs(diffX) > threshold && velocityX > velocity_threshold){
                        if (diffX > 0)
                            onSwipeRight();
                        else
                            onSwipeLeft();
                        result = true;
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

        public void onSwipeTop(){}
        public void onSwipeDown(){}
        public void onSwipeLeft(){}
        public void onSwipeRight(){}

    }
}
