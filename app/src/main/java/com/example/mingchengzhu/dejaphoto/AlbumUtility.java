package com.example.mingchengzhu.dejaphoto;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;

/**
 * Created by andymeza on 4/29/17.
 */

public class AlbumUtility {

    private final static String path = Environment.getExternalStorageDirectory().toString();
    private final static String filePath = "/dejaphoto/media/app images/";

    public static void createAlbum()
    {
        File dir = new File(path, filePath);
        dir.mkdirs();
    }

    public static boolean dejaAlbumExists()
    {
        return albumDir().isDirectory();
    }

    public static File albumDir()
    {
        File dir = new File(path, filePath);
        return dir;
    }

    public static boolean containsPhotos()
    {
        return (albumDir().listFiles().length!=0);
    }

    public static int numberOfPhotos()
    {
        return albumDir().listFiles().length;
    }

    public static void insertPhoto(Uri image, Context context) throws IOException
    {

        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(image, proj, null, null, null);
        if(cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        File sourceFile = new File(res);
        File destFile = new File(albumDir(), java.util.UUID.randomUUID().toString());
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }

    public static boolean releasePhoto(String removePhoto) throws IOException
    {
        File photoPath = new File(removePhoto);

        return photoPath.delete();
    }

    public static File[] returnPhotoList()
    {
        return albumDir().listFiles();
    }


}




