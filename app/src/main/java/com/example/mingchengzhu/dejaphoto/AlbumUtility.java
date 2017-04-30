package com.example.mingchengzhu.dejaphoto;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.net.URI;

/**
 * Created by andymeza on 4/29/17.
 */

public class AlbumUtility {

    private final static String path = Environment.getExternalStorageDirectory().toString();
    private final static String filePath = "/dejaphoto/media/app images/";

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

  /*  public static void insertPhoto(Uri image)
    {
        String photoPath = image.getPath();

        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();



        //scan the image so show up in album
        MediaScannerConnection.scanFile(this,
                new String[] { photoPath }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        if(Bitmap.Config.LOG_DEBUG_ENABLED) {
                            Log.d(Config.LOGTAG, "scanned : " + path);
                        }
                    }
                });


    } */



}
