/*
 * DejaPhoto class
 * Description: Represents a single photo added to the app by the user
 * Created by sterling on 5/6/17.
 */

package com.example.mingchengzhu.dejaphoto;

import android.content.ContentResolver;
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

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a single photo added to the app by the user
 */
public class DejaPhoto {

    public static final String PHOTO_KEY_KCOUNT = "karmaCount";
    public static final String PHOTO_KEY_LATITUDE = "latitude";
    public static final String PHOTO_KEY_LONGITUDE = "longitude";
    public static final String PHOTO_KEY_LNAME = "locationName";
    public static final String PHOTO_KEY_TIME_TAKEN = "timeTaken";
    public static final String PHOTO_KEY_PICTURE_ORIGIN = "pictureOrigin";
    public static final String PHOTO_KEY_FROM_CAMERA = "isFromCamera";
    public static final String PHOTO_KEY_FILE_EXT = "fileExtension";

    /**
     * Used for logging
     */
    private static final String TAG = "DejaPhoto";

    /**
     * A string uniquely identifying this photo
     */
    private String id;

    /**
     * The file extension
     */
    private String fileExtension;

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
     * Cached name of the location
     */
    private String locationName;

    /**
     * ID of the person whose picture it is
     */
    private String pictureOrigin;

    // for indicating userdefined location
    public boolean userDefinedLocation = false;

    private boolean isFromCamera;

    /**
     * Contructor from map objects from Firebase
     */
    public DejaPhoto(Map<String, Object> map, String id) {
        this.id = id;
        this.fileExtension = (String)map.get(PHOTO_KEY_FILE_EXT);
        this.hasKarma = false;
        this.wasReleased = false;
        this.time = ((Number)map.get(PHOTO_KEY_TIME_TAKEN)).longValue();

        Number latitude = (Number)map.get(PHOTO_KEY_LATITUDE);
        Number longitude = (Number)map.get(PHOTO_KEY_LONGITUDE);
        if (latitude != null && longitude != null) {
            this.location = new Location("");
            this.location.setLatitude(latitude.doubleValue());
            this.location.setLongitude(longitude.doubleValue());
        }

        this.locationName = (String)map.get(PHOTO_KEY_LNAME);
        this.pictureOrigin = (String)map.get(PHOTO_KEY_PICTURE_ORIGIN);
        this.isFromCamera = (Boolean)map.get(PHOTO_KEY_FROM_CAMERA);

        /*String myUsername = MainActivity.getCurrentUser();
        boolean isFriend = ! myUsername.equalsIgnoreCase(pictureOrigin);
        String targetAlbum = AlbumUtility.albumForParameters(isFriend, isFromCamera);*/
    }

    /**
     * Constructor to be used only with JUnit tests
     * @param id a unique string identifying this photo
     * @param hasKarma true if this photo has karma
     * @param wasReleased true if this photo was released
     * @param time seconds since 1970 until the time the photo was taken
     */
    public DejaPhoto(String userID, String id, boolean hasKarma, boolean wasReleased, long time) {
        this.id = id;
        this.pictureOrigin = userID;
        this.isFromCamera = false;
        this.hasKarma = hasKarma;
        this.wasReleased = wasReleased;
        this.time = time;
        location = null;
    }

    /**
     * Constructor to be used only with JUnit tests
     * @param id a unique string identifying this photo
     * @param latitude the latitude where this photo was taken
     * @param longitude the latitude where this photo was taken
     * @param hasKarma true if this photo has karma
     * @param wasReleased true if this photo was released
     * @param time seconds since 1970 until the time the photo was taken
     */
    public DejaPhoto(String userID, String id, double latitude, double longitude, boolean hasKarma, boolean wasReleased, long time) {
        this.id = id;
        this.pictureOrigin = userID;
        this.isFromCamera = false;
        this.hasKarma = hasKarma;
        this.wasReleased = wasReleased;
        this.time = time;
        location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    /**
     * Preferred constructor
     * @param id a string uniquely identifying this photo
     *
     * @param contentResolver a ContentResolver that can be used to query
     */
    public DejaPhoto(String userId, String id, Uri galleryUri, ContentResolver contentResolver) {
        this.id = id;
        this.pictureOrigin = userId;
        hasKarma = false;
        wasReleased = false;

        // Get all the info from the content resolver

        final String[] columns = {
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Audio.Media.DATE_ADDED
        };

        Cursor cursor = contentResolver.query(galleryUri, columns, null, null, null);

        if (cursor != null) {
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
                    Log.w(TAG, "Encountered a photo with a lat/lon that don't parse as a double!", e);
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
    }

    /**
     * Determines if two DejaPhoto objects are equal
     * @param other the other photo to compare with
     * @return true if both have equivalent URI
     */
    public boolean equals(DejaPhoto other) {
        return other != null && id != null && other.id != null && id.equals(other.id);
    }

    // Getters are setters

    static public File fileForParameters(String id, String fileExtension, boolean isFromCamera, boolean isFromFriend) {
        String targetAlbum = AlbumUtility.albumForParameters(isFromFriend, isFromCamera);
        return fileForParameters(id, fileExtension, targetAlbum);
    }

    static public File fileForParameters(String id, String fileExtension, String targetAlbum) {
        File destFolder = new File(AlbumUtility.albumParentFolder(), targetAlbum);
        return new File(destFolder, id + fileExtension);
    }

    static public String generateNewId() {
        return UUID.randomUUID().toString();
    }

    public File getFile() {
        String myUsername = MainActivity.getCurrentUser();
        boolean isFromFriend = ! myUsername.equalsIgnoreCase(pictureOrigin);

        return DejaPhoto.fileForParameters(id, getFileExtension(), isFromCamera, isFromFriend);
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


    /**
     * @param context the context that will be used to retrieve the absolute path of a Uri
     * @return DejaPhoto member variables in string format.
     */
    public String toString(Context context)
    {
        return (this.getFile().getAbsolutePath() + "\n" +
                this.getKarma() + "\n" +
                this.getReleased() + "\n" +
                this.getTime() + "\n" );
    }

    /**
     *
     * @param context the context that will be used to retrieve the absolute path of a Uri
     * @param uri the uri whose absolute path will be returned
     * @return the absolute file path of the parameter Uri
     */
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

    /**
     * Cached name of the location
     */
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getPictureOrigin() {
        return pictureOrigin;
    }

    public void setPictureOrigin(String pictureOrigin) {
        this.pictureOrigin = pictureOrigin;
    }

    public boolean isFromCamera() {
        return isFromCamera;
    }

    public void setFromCamera(boolean fromCamera) {
        isFromCamera = fromCamera;
    }

    public String getId() {
        return id;
    }

    public String getFileExtension() {
        if (fileExtension == null) {
            return ".jpg";
        }

        return fileExtension;
    }

}

