package com.example.mingchengzhu.dejaphoto;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by andymeza on 4/29/17.
 */

public class AlbumUtility {
    // Used internally for logging
    private static final String TAG = "AlbumUtility";

    /**
     * Album for photos copied from other albums
     */
    public static final String ALBUM_COPIED = "DejaPhotoCopied";

    /**
     * Album for photos taken in the app
     */
    public static final String ALBUM_INAPP_CAMERA = "DejaPhoto";

    /**
     * Album for friend's photos downloaded by the app
     */
    public static final String ALBUM_FRIENDS = "DejaPhotoFriends";

    public static File albumParentFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    public static String[] allAlbumNames() {
        return new String[]{ALBUM_INAPP_CAMERA, ALBUM_COPIED, ALBUM_FRIENDS};
    }

    public static String albumForParameters(boolean isFriend, boolean fromCamera) {
        return isFriend ? ALBUM_FRIENDS : fromCamera ? ALBUM_INAPP_CAMERA : ALBUM_COPIED;
    }

    public static boolean albumsExist() {
        File parentFolder = albumParentFolder();

        for (String albumName : allAlbumNames()) {
            File path = new File(parentFolder, albumName);
            if (!path.exists()) {
                Log.w(TAG, "Reporting that we don't have a complete set of albums because the following album doesn't exist: " + albumName);
                return false;
            }
        }

        return true;
    }

    public static boolean createAlbums() {
        File parentFolder = albumParentFolder();

        for (String albumName : allAlbumNames()) {
            File path = new File(parentFolder, albumName);
            if (!path.exists()) {
                path.mkdirs();
            }
        }

        if (albumsExist()) {
            Log.i(TAG, "successfully created all albums");
            return true;
        } else {
            Log.e(TAG, "Failed to create albums! Check storage permission");
            return false;
        }
    }

    @Nullable
    private static DejaPhoto addPhoto(String targetAlbum, Uri photoUri, Context applicationContext) {
        createAlbums();

        ContentResolver contentResolver = applicationContext.getContentResolver();

        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(photoUri, proj, null, null, null);
        if (cursor == null) {
            Log.e(TAG, "contentResolver.query returned null!");
            return null;
        }

        if(cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        if (res == null) {
            Log.e(TAG, "failed to get file path for source image in gallery");
            return null;
        }

        File sourceFile = new File(res);
        File destFile = createNewPhotoFilename(targetAlbum, null);
        if (!sourceFile.exists()) {
            return null;
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }
        } catch (IOException e) {
            Log.e(TAG, "failed to copy a photo to " + ALBUM_COPIED);
            e.printStackTrace();

            destFile = null;
        } finally {
            try {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            } catch (IOException e) {
                Log.w(TAG, "failed to close a file while copying a photo to " + ALBUM_COPIED);
            }
        }

        String[] paths = {destFile.getAbsolutePath()};
        MediaScannerConnection.scanFile(applicationContext,
                paths,
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // scanned path and uri
                    }
                });

        return new DejaPhoto(destFile, photoUri, contentResolver);
    }

    @Nullable
    public static DejaPhoto addInAppCameraPhoto(Uri cameraPhoto, Context applicationContext) {
        DejaPhoto photo = addPhoto(ALBUM_INAPP_CAMERA, cameraPhoto, applicationContext);

        photo.setPictureOrigin(MainActivity.getCurrentUser());
        photo.setFromCamera(true);

        return photo;
    }

    @Nullable
    public static DejaPhoto addGalleryPhoto(Uri galleryPhoto, Context applicationContext) {
        DejaPhoto photo = addPhoto(ALBUM_COPIED, galleryPhoto, applicationContext);

        photo.setPictureOrigin(MainActivity.getCurrentUser());
        photo.setFromCamera(false);

        return photo;
    }

    @Nullable
    public static File createNewPhotoFilename(String targetAlbum, String filename) {
        if (filename == null || filename.length() == 0) {
            filename = java.util.UUID.randomUUID().toString() + ".jpg";
        }

        File destFolder = new File(albumParentFolder(), targetAlbum);
        return new File(destFolder, filename);
    }

    @Nullable
    public static void releasePhoto(DejaPhoto photo)
    {
        if (photo.getFile().delete())
        {
            Log.e(TAG, "Failed to delete: " + photo.getFile().getPath());
        }
    }



}
