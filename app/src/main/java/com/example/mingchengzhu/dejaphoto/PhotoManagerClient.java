package com.example.mingchengzhu.dejaphoto;

/**
 * Created by sterling on 5/14/17.
 */

public interface PhotoManagerClient {

    public enum SwipeDirection {
        right, left, neither
    }
    public SwipeDirection lastSwipe = SwipeDirection.neither;

    public Tracker tracker = new Tracker();

    public void currentPhotoChanged();
}
