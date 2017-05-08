package com.example.mingchengzhu.dejaphoto;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
//import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by sterling on 5/6/17.
 */

public class DejaPhoto {
    private static DejaPhoto[] currentSearchResults;

    private Uri galleryUri;
    private boolean hasKarma;
    private boolean wasReleased;
    private long time;

    private Location location;

    // Note: will be moved to a different class
    static DejaPhoto[] getCurrentSearchResults() {
        return currentSearchResults;
    }

    DejaPhoto(Uri galleryUri, Context context) {
        this.galleryUri = galleryUri;
        hasKarma = false;
        wasReleased = false;

        String[] columns = {
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Audio.Media.DATE_ADDED
        };

        Cursor cursor = context.getContentResolver().query(galleryUri,
                columns, null, null, null);
        cursor.moveToFirst();

        int latIndex = cursor.getColumnIndex(columns[0]);
        String latitude = cursor.getString(latIndex);

        int lonIndex = cursor.getColumnIndex(columns[1]);
        String longitude = cursor.getString(lonIndex);

        if (latitude != null && longitude != null) {
            try {
                location = new Location("");
                location.setLatitude(Double.parseDouble(latitude));
                location.setLongitude(Double.parseDouble(longitude));
            } catch (NumberFormatException e) {
                location = null;
            }
        } else {
            location = null;
        }

        int dateIndex = cursor.getColumnIndex(columns[2]);
        time = cursor.getLong(dateIndex);

        cursor.close();
    }

    public boolean equals(DejaPhoto other) {
        return galleryUri != null && other.galleryUri != null && galleryUri.equals(other.galleryUri);
    }

    public Uri getUri() {
        return galleryUri;
    }

    public boolean getKarma() {
        return hasKarma;
    }

    public void setKarma(boolean karma) {
        hasKarma = karma;
    }

    public boolean getReleased() {
        return wasReleased;
    }

    public void setReleased(boolean released) {
        wasReleased = released;
    }

    public long getTime() {
        return time;
    }

    public Location getLocation() {
        return location;
    }
}
