package com.example.mingchengzhu.dejaphoto;

/**
 * Created by sterling on 5/14/17.
 */

/**
 * Interface to be implemented by classes that use PhotoManager
 */
public interface PhotoManagerClient {

    /**
     * The most recent swipe direction
     */
    public enum SwipeDirection {
        right, left, neither
    }
    public SwipeDirection lastSwipe = SwipeDirection.neither;

    /**
     * A composed Tracker object for information on the user's current time and location
     */
    public Tracker tracker = new Tracker();

    /**
     * A callback method to inform the client that the currentPhoto field has changed
     */
    public void currentPhotoChanged();
}
