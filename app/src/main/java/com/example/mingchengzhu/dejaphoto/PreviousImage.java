package com.example.mingchengzhu.dejaphoto;

import android.util.Log;

import java.util.LinkedList;

/**
 * Created by ruihanzeng on 5/8/17.
 */

public class PreviousImage {
    // Used for logging
    private static final String TAG = "PreviousImage";

    private LinkedList<DejaPhoto> previousPhotos;
    DejaPhoto currentPhoto;

    public PreviousImage() {
        previousPhotos = new LinkedList<DejaPhoto>();
        currentPhoto = null;
    }

    /**
     * similar to a push
     * adds newest photo to photo deque. If already 10 photos in deque,
     * oldest one is removed
     *
     * @param newCurrentPhoto the new current photo
     */
    public void setCurrentPhoto(DejaPhoto newCurrentPhoto) {
        if (newCurrentPhoto == null) {
            Log.w(TAG, "setCurrentPhoto called with null!!!");
            return;
        }

        // if we have a currentPhoto, push it
        if (currentPhoto != null) {
            previousPhotos.addLast(currentPhoto);
        }

        currentPhoto = newCurrentPhoto;
    }

    /**
     * similar to a pop
     * returns the previous photo
     * note: null is returned if no previous photo is avalible
     *
     * @return the previous photo
     */
    public DejaPhoto swipeLeft() {
        DejaPhoto previous = previousPhotos.pollLast();
        if (previous != null) {
            // this is the new current photo
            currentPhoto = previous;
        }

        return previous;
    }
    
    /**
     * similar to a top
     * returns previous image without removing it
     * note: null is returned if no previous photo is avalible
     *
     * @return the previous photo
     */
    public DejaPhoto getCurrentPhoto() {
        return currentPhoto;
    }
    

    /**
     * checks if a photo has been previously seen before or not
     *
     * @param photo photo being checked
     * @return true if photo is in the deque and false if not
     */
    public boolean PhotoPreviouslySeen(DejaPhoto photo) {
        if (photo == null) {
            Log.w(TAG, "PhotoPreviouslySeen called with null!!!");
            return false;
        }

        if (currentPhoto != null && currentPhoto.equals(photo)) {
            return true;
        }

        return previousPhotos.contains(photo);
    }

    /**
     * @return the number of images currently being stored
     */
    public boolean canSwipeBack() {
        return previousPhotos.size() > 0;
    }

}
