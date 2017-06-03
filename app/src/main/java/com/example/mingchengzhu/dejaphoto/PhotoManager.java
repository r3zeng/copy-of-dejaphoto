package com.example.mingchengzhu.dejaphoto;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by mingchengzhu on 5/13/17.
 * Description: includes weighting algorithm and next/prev function.
 */

public class PhotoManager {
    PhotoManagerClient client;
    private static DejaPhoto[] allPhotos = {};
    private DejaPhoto currentPhoto = null;
    private boolean matchTime = true;
    private boolean matchDate = true;
    private boolean matchLocation = true;
    private boolean matchKarma = true;
    private boolean showMine = true;
    private boolean showFriends = true;
    private PreviousImage backHistory = new PreviousImage();

    public PhotoManager(PhotoManagerClient client) {
        this.client = client;
    }

    public static DejaPhoto[] getAllPhotos() {
        return allPhotos;
    }

    public static DejaPhoto addPhotoWithUri(Uri photoUri, Context context) {
        DejaPhoto newPhoto = new DejaPhoto(photoUri, context);

        // Check for duplicate
        for (DejaPhoto photo : allPhotos) {
            if (photo.equals(newPhoto) && !photo.getReleased()) {
                return photo;
            }
        }

        DejaPhoto[] newList = new DejaPhoto[allPhotos.length + 1];
        newList[0] = newPhoto;
        if (allPhotos.length > 0) {
            System.arraycopy(allPhotos, 0, newList, 1, allPhotos.length);
        }

        allPhotos = newList;

        return newPhoto;
    }

    /**
     * gets the next Image to display
     * should be called on right swipe and by the auto-switcher
     */
    public DejaPhoto getNextRandomImage() {

        if (allPhotos == null || allPhotos.length == 0) {
            Log.e(TAG, "Error: getting next image from empty album");
            return null;
        }

        double largestWeight = -1;
        DejaPhoto selectedPhoto = null;

        for (DejaPhoto currentPhoto : allPhotos) {
            double photoWeight = getTotalPhotoWeight(currentPhoto);
            if (photoWeight > largestWeight) {
                selectedPhoto = currentPhoto;
                largestWeight = photoWeight;
            }
        }

        return selectedPhoto;
    }

    /**
     * helper function for getNextRandomImage
     * gets the weight for the probality a photo is displayed
     *
     * @param photo deja photo object
     * @return a value repersenting the likelyness this photo is to be displayed as the background.
     * note: This value is not a percentage and should be compared relative to other photo weights
     */
    public double getTotalPhotoWeight(DejaPhoto photo) {
        Log.d(TAG, "getTotalPhotoWeight called");
        Random rand = new Random();
        double rand_value = rand.nextDouble();
        return rand_value * getTimeWeight(photo) * getKarmaWeight(photo) * getReleasedWeight(photo)
                * getDateWeight(photo) * getLocationWeight(photo) * getRecentWeight(photo)
                * getSameDayWeight(photo) * getLastPhotoWeight(photo) * getShowFriendsWeight()
                * getShowMineWeight();
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return time weight
     */
    public double getTimeWeight(DejaPhoto photo) {
        Log.d(TAG, "getTimeWeight called");
        if (!matchTime) { /*time from deja mode disabled*/
            return 1; //base weight
        } else if (client.tracker == null || client.tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid data
        } else {
            long SystemTime = client.tracker.getTime();
            long PhotoTime = photo.getTime();

            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_2_HOURS = 7200000;

            long difference = Math.abs(SystemTime - PhotoTime) % MILLISECONDS_IN_DAY;

            if (difference < MILLISECONDS_IN_2_HOURS) {
                return 4;
            } else {
                return 1;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return date weight
     */
    public double getDateWeight(DejaPhoto photo) {
        Log.d(TAG, "getDateWeight called");
        if (!matchDate) {
            return 1;
        } else if (client.tracker == null || client.tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid datat
        } else {
            long SystemTime = client.tracker.getTime();
            long PhotoTime = photo.getTime();

            long difference = Math.abs(SystemTime - PhotoTime);

            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_WEEK = 7 * MILLISECONDS_IN_DAY;
            final long MILLISECONDS_IN_MONTH = 30 * MILLISECONDS_IN_DAY;
            final long MILLISECONDS_IN_3_MONTH = 3 * MILLISECONDS_IN_MONTH;
            final long MILLISECONFS_IN_6_MONTH = 6 * MILLISECONDS_IN_MONTH;

            if (difference < MILLISECONDS_IN_DAY) {
                return 4;
            } else if (difference < MILLISECONDS_IN_WEEK) {
                return 3.4;
            } else if (difference < MILLISECONDS_IN_MONTH) {
                return 2.8;
            } else if (difference < MILLISECONDS_IN_3_MONTH) {
                return 2;
            } else if (difference < MILLISECONFS_IN_6_MONTH) {
                return 1.4;
            } else {
                return 1;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return location weight
     */
    public double getLocationWeight(DejaPhoto photo) {
        Log.d(TAG, "getLocationWeight called");
        if (!matchLocation) {
            return 1; //base weight
        } else if (client.tracker == null || client.tracker.getLocation() == null || photo.getLocation() == null) {
            return 1;//invalid data
        } else {
            Location SystemLocation = client.tracker.getLocation();
            Location PhotoLocation = photo.getLocation();

            double DistanceInMeters = SystemLocation.distanceTo(PhotoLocation);

            if (DistanceInMeters < 1000) {
                return 4;
            } else {
                return 1;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return Karma weight
     */
    public double getKarmaWeight(DejaPhoto photo) {
        Log.d(TAG, "getKarmaWeight called");
        if (!matchKarma) {
            return 1;
        } else {
            if (photo.getKarma()) {
                return 4;
            } else {
                return 1;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return release weight
     */
    public double getReleasedWeight(DejaPhoto photo) {
        Log.d(TAG, "getReleasedWeight called");
        if (photo.getReleased()) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * shoulf not be called elsewhere
     *
     * @param photo
     * @return recent weight
     */
    public double getRecentWeight(DejaPhoto photo) {
        Log.d(TAG, "getRecentWeight called");
        if (backHistory != null && backHistory
                .PhotoPreviouslySeen(photo)) {
            return 0.1;
        } else {
            return 1;
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @param photo
     * @return same day weight
     */
    public double getSameDayWeight(DejaPhoto photo) {
        Log.d(TAG, "getSameDatWeight called");
        if (!matchDate) {
            return 1;
        } else if (client.tracker == null || client.tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid data
        } else {
            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_WEEK = 7 * MILLISECONDS_IN_DAY;

            long CurrentTime = client.tracker.getTime();
            long PhotoTime = photo.getTime();

            CurrentTime = CurrentTime % MILLISECONDS_IN_WEEK;
            PhotoTime = PhotoTime % MILLISECONDS_IN_WEEK;

            long CurrentDay = CurrentTime / MILLISECONDS_IN_DAY;
            long PhotoDay = PhotoTime / MILLISECONDS_IN_DAY;

            if (CurrentDay == PhotoDay) {
                return 4;
            } else {
                return 1;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @param photo
     * @return % chance of getting same photo twice unless only 1 photo
     */
    public double getLastPhotoWeight(DejaPhoto photo) {
        Log.d(TAG, "getLastPhotoWeight called");
        if (backHistory == null || backHistory.getNumberofPhoto() == 1) {
            return 1;
        } else if (photo != null
                && backHistory.getCurrentPhoto() != null
                && backHistory.getCurrentPhoto().equals(photo)) {
            return 0;
        } else {
            return 1;
        }
    }
    public double getShowMineWeight(DejaPhoto photo) {
        Log.d(TAG, "getShowMineWeight called");
        if (showMine){
            if (photo.getPictureOrigin().equals(MainActivity.getCurrentUser()))
                return 1;
            else
                return 0;
        }
        else {
            if (photo.getPictureOrigin().equals(MainActivity.getCurrentUser()))
                return 0;
            else
                return 1;
        }

    }
    public double getShowFriendsWeight(DejaPhoto photo) {
        Log.d(TAG, "getShowFriendsWeight called");
        if (showFriends) {
            if (photo.getPictureOrigin().equals(MainActivity.getCurrentUser()))
                return 0;
            else
                return 1;
        } else {
            if (photo.getPictureOrigin().equals(MainActivity.getCurrentUser()))
                return 1;
            else
                return 0;
        }
    }


    public void next() {
        Log.d(TAG, "next called");
        DejaPhoto newPhoto = getNextRandomImage();
        if (newPhoto == null) {
            Log.w(TAG, "getNextRandomImage() returned null");
            // failed to determine what photo is next, so abort
            return;
        }

        currentPhoto = newPhoto;
        backHistory.swipeRight(currentPhoto);

        client.currentPhotoChanged();
    }

    public void prev() {
        Log.d(TAG, "prev called");
        DejaPhoto previous = backHistory.swipeLeft();
        if (previous == null) {
            // there are no previous photos, so abort
            return;
        }

        currentPhoto = previous;

        client.currentPhotoChanged();
    }

    public boolean getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(boolean matchTime) {
        this.matchTime = matchTime;
    }

    public boolean getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(boolean matchDate) {
        this.matchDate = matchDate;
    }

    public boolean getMatchLocation() {
        return matchLocation;
    }

    public void setMatchLocation(boolean matchLocation) {
        this.matchLocation = matchLocation;
    }

    public boolean getMatchKarma() {
        return matchKarma;
    }

    public void setMatchKarma(boolean matchKarma) {
        this.matchKarma = matchKarma;
    }

    public boolean getShowFriends() {
        return showFriends;
    }

    public void setShowFriends(boolean showFriends) {
        this.showFriends = showFriends;
    }

    public boolean getShowMine() { return showMine; }

    public void setShowMine(boolean showMine) {
        this.showMine = showMine;
    }

    public DejaPhoto getCurrentPhoto() {
        return currentPhoto;
    }

    public void setCurrentPhoto(DejaPhoto currentPhoto) {
        this.currentPhoto = currentPhoto;
    }

    public PreviousImage getBackHistory() {
        return backHistory;
    }
}

