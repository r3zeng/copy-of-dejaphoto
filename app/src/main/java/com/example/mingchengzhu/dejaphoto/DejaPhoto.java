package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by sterling on 5/6/17.
 */

public class DejaPhoto {
    private static DejaPhoto[] currentSearchResults;

    private Uri galleryUri;
    private boolean hasKarma;
    private boolean wasReleased;

    // Note: will be moved to a different class
    static DejaPhoto[] getCurrentSearchResults() {
        return currentSearchResults;
    }

    Uri getUri() {
        return galleryUri;
    }

    boolean getKarma() {
        return hasKarma;
    }

    boolean getReleased() {
        return wasReleased;
    }

    long getTime() {
        return 0;
    }

    Location getLocation() {
        return new Location("");
    }
}
