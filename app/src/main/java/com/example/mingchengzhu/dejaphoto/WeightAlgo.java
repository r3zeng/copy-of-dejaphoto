package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.util.Log;

import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by mingchengzhu on 5/13/17.
 */

public class WeightAlgo {
    /**
     * gets the next Image to display
     * should be called on right swipe and by the auto-switcher
     */
    MainActivity activity;

    public WeightAlgo(MainActivity activity) {
        this.activity = activity;
    }

    public DejaPhoto getNextRandomImage() {

        DejaPhoto[] list = DejaPhoto.getCurrentSearchResults();
        if (list == null || list.length == 0) {
            Log.e(TAG, "Error: getting next image from empty album");
            return null;
        }

        double largestWeight = -1;
        DejaPhoto selectedPhoto = null;

        for (DejaPhoto currentPhoto : list) {
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
    private double getTotalPhotoWeight(DejaPhoto photo) {
        Random rand = new Random();
        double rand_value = rand.nextDouble();
        return rand_value * getTimeWeight(photo) * getKarmaWeight(photo) * getRelasedWeight(photo)
                * getDateWeight(photo) * getLocationWeight(photo) * getRecentWeight(photo)
                * getSameDayWeight(photo) * getLastPhotoWeight(photo);
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return time weight
     */
    private double getTimeWeight(DejaPhoto photo) {
        if (!activity.Deja_Time) { /*time from deja mode disabled*/
            return 1; //base weight
        } else if (activity.tracker == null || activity.tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid data
        } else {
            long SystemTime = activity.tracker.getTime();
            long PhotoTime = photo.getTime();

            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_2_HOURS = 7200000;

            long difference = Math.abs(SystemTime - PhotoTime) % MILLISECONDS_IN_DAY;

            if (difference < MILLISECONDS_IN_2_HOURS) {
                return 2;
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
    private double getDateWeight(DejaPhoto photo) {
        if (!activity.Deja_Date) {
            return 1;
        } else if (activity.tracker == null || activity.tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid datat
        } else {
            long SystemTime = activity.tracker.getTime();
            long PhotoTime = photo.getTime();

            long difference = Math.abs(SystemTime - PhotoTime);

            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_WEEK = 7 * MILLISECONDS_IN_DAY;
            final long MILLISECONDS_IN_MONTH = 30 * MILLISECONDS_IN_DAY;
            final long MILLISECONDS_IN_3_MONTH = 3 * MILLISECONDS_IN_MONTH;
            final long MILLISECONFS_IN_6_MONTH = 6 * MILLISECONDS_IN_MONTH;

            if (difference < MILLISECONDS_IN_DAY) {
                return 2;
            } else if (difference < MILLISECONDS_IN_WEEK) {
                return 1.7;
            } else if (difference < MILLISECONDS_IN_MONTH) {
                return 1.4;
            } else if (difference < MILLISECONDS_IN_3_MONTH) {
                return 1;
            } else if (difference < MILLISECONFS_IN_6_MONTH) {
                return 0.7;
            } else {
                return 0.5;
            }
        }
    }

    /**
     * helper function for getTotalPhotoWeight
     * should not be called elsewhere
     *
     * @return location weight
     */
    private double getLocationWeight(DejaPhoto photo) {
        if (!activity.Deja_Location) {
            return 1; //base weight
        } else if (activity.tracker == null || activity.tracker.getLocation() == null || photo.getLocation() == null) {
            return 1;//invalid data
        } else {
            Location SystemLocation = activity.tracker.getLocation();
            Location PhotoLocation = photo.getLocation();

            double DistanceInMeters = SystemLocation.distanceTo(PhotoLocation);

            if (DistanceInMeters < 200) {
                return 2;
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
    private double getKarmaWeight(DejaPhoto photo) {
        if (!activity.Deja_Karma) {
            return 1;
        } else {
            if (photo.getKarma()) {
                return 2;
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
    private double getRelasedWeight(DejaPhoto photo) {
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
    private double getRecentWeight(DejaPhoto photo) {
        if (activity.previousImage != null && activity.previousImage.PhotoPreviouslySeen(photo)) {
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
    private double getSameDayWeight(DejaPhoto photo) {
        if (!activity.Deja_Date) {
            return 1;
        } else if (activity.tracker == null || activity.tracker.getTime() == 0 || photo.getTime() == 0) {
            return 1;//invalid data
        } else {
            final long MILLISECONDS_IN_DAY = 86400000;
            final long MILLISECONDS_IN_WEEK = 7 * MILLISECONDS_IN_DAY;

            long CurrentTime = activity.tracker.getTime();
            long PhotoTime = photo.getTime();

            CurrentTime = CurrentTime % MILLISECONDS_IN_WEEK;
            PhotoTime = PhotoTime % MILLISECONDS_IN_WEEK;

            long CurrentDay = CurrentTime / MILLISECONDS_IN_DAY;
            long PhotoDay = PhotoTime / MILLISECONDS_IN_DAY;

            if (CurrentDay == PhotoDay) {
                return 2;
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
     * @return 0% chance of getting same photo twice unless only 1 photo
     */
    private double getLastPhotoWeight(DejaPhoto photo) {
        if (activity.previousImage == null || activity.previousImage.getNumberofPhoto() == 1) {
            return 1;
        } else if (activity.previousImage.getLastPhoto().equals(photo)) {
            return 0;
        } else {
            return 1;
        }
    }

    public void next() {
        //put switch wallpaper method here
        activity.CurrentPhoto = getNextRandomImage();

        if (activity.CurrentPhoto != null) {
            if (activity.lastSwipe == MainActivity.SwipeDirection.left) {
                activity.CurrentPhoto = getNextRandomImage();
            }
            activity.setBackgroundImage(activity.CurrentPhoto);
            activity.setWallpaper(activity.CurrentPhoto);

            activity.previousImage.swipeRight(activity.CurrentPhoto);
        }
    }

    public void prev() {
        //put switch wallpaper method here
        activity.CurrentPhoto = activity.previousImage.swipeLeft();
        if (activity.CurrentPhoto != null) {
            if (activity.lastSwipe == MainActivity.SwipeDirection.right) {
                activity.CurrentPhoto = activity.previousImage.swipeLeft();
            }
            activity.setBackgroundImage(activity.CurrentPhoto);
            activity.setWallpaper(activity.CurrentPhoto);
        }
    }
}

