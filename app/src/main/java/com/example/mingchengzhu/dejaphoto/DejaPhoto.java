package com.example.mingchengzhu.dejaphoto;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
//import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

/**
 * Created by sterling on 5/6/17.
 */

/**
 * Represents a single photo added to the app by the user
 */
public class DejaPhoto {
    /**
     * Used for logging
     */
    private static final String TAG = "DejaPhoto";

    /**
     * A URI from the user's gallery that uniquely identifies this photo
     */
    private Uri galleryUri;

    /**
     * true if this photo has been given karma, meaning it should appear more frequently
     */
    private boolean hasKarma;

    /**
     * true if this photo was released, meaning it should never appear again
     */
    private boolean wasReleased;

    /**
     * Seconds since 1970 until the time the photo was taken
     */
    private long time;

    /**
     * The location where this photo was taken, or null if not available
     */
    private Location location;

    /**
     * true if our save state files reference this photo
     */
    private boolean savedToFile;

    /**
     * Constructor to be used only with JUnit tests
     * @param galleryUriString a unique string which will be converted to a URI
     * @param latitude the latitude where this photo was taken
     * @param longitude the latitude where this photo was taken
     * @param hasKarma true if this photo has karma
     * @param wasReleased true if this photo was released
     * @param time seconds since 1970 until the time the photo was taken
     */
    public DejaPhoto(String galleryUriString, double latitude, double longitude, boolean hasKarma, boolean wasReleased, long time) {
        this.galleryUri = Uri.parse(galleryUriString);
        this.hasKarma = hasKarma;
        this.wasReleased = wasReleased;
        this.time = time;
        location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    /**
     * Preferred constructor
     * @param galleryUri a URI pointing into the user's photo gallery to a specific photo
     * @param context a Context (such as an Activity) which can be used for getContentResolver()
     */
    public DejaPhoto(Uri galleryUri, Context context) {
        this.galleryUri = galleryUri;
        hasKarma = false;
        wasReleased = false;

        // Get all the info from the content resolver

        String[] columns = {
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Audio.Media.DATE_ADDED
        };

        Cursor cursor = context.getContentResolver().query(galleryUri,
                columns, null, null, null);
        cursor.moveToFirst();

        // Now set the location

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
                Log.w(TAG, "The user's gallery contains a photo with a lat/lon that don't parse as a double!", e);
                location = null;
            }
        } else {
            location = null;
        }

        // And set the time

        int dateIndex = cursor.getColumnIndex(columns[2]);
        time = cursor.getLong(dateIndex);

        cursor.close();
    }

    /**
     * Determines if two DejaPhoto objects are equal
     * @param other the other photo to compare with
     * @return true if both have equivalent URI
     */
    public boolean equals(DejaPhoto other) {
        return galleryUri != null && other.galleryUri != null && galleryUri.equals(other.galleryUri);
    }

    // Getters are setters

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

    public boolean getSavedToFile() {
        return savedToFile;
    }

    public void setReleased(boolean released) {
        wasReleased = released;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long timeStamp) {time = timeStamp; }

    public Location getLocation() {
        return location;
    }


    //Andy

    /**
     * @name toString()
     * @param context
     * @return DejaPhoto member variables in string format.
     */
    public  String toString(Context context)
    {
        return (getPathFromUri(context, this.getUri()) + "\n" +
                this.getKarma() + "\n" +
                this.getReleased() + "\n" +
                this.getTime() + "\n" );
    }

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

}

