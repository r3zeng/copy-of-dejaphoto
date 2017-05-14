/*
 * Created by sterling on 5/14/17.
 * Description: Interface to be implemented by classes that use PhotoManager
 */

package com.example.mingchengzhu.dejaphoto;

/**
 * Interface to be implemented by classes that use PhotoManager
 */
public interface PhotoManagerClient {
    /**
     * A composed Tracker object for information on the user's current time and location
     */
    Tracker tracker = new Tracker();

    /**
     * A callback method to inform the client that the currentPhoto field has changed
     */
    void currentPhotoChanged();
}
