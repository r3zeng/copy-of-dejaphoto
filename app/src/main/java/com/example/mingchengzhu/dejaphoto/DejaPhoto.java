package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by sterling on 5/6/17.
 */

public class DejaPhoto {
    static DejaPhoto[] currentSearchResults;

    private Uri galleryUri;
    boolean hasKarma;
    boolean wasReleased;

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

    }

    Location getLocation() throws IOException {
        //ExifInterface exif = new ExifInterface(galleryUri.getPath());
        //return exif
    }
}
