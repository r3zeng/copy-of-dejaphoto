package com.example.mingchengzhu.dejaphoto;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by tianhuic on 6/6/17.
 */

public class Scaling {
    private static String TAG = "Scaling";

    public static File scaling(DejaPhoto photo) {
        File photoFile = photo.getFile();

        Log.i(TAG, "attempting to scale an existing image photoFile");

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

        final int originalWidth = bitmap.getWidth();
        final int originalHeight = bitmap.getHeight();

        final int maxWidth = 1080;
        final int maxHeight = 1920;

        Log.i(TAG, "original dimensions: " + bitmap.getWidth() + "x" + bitmap.getHeight());

        int scaleFactor = 2;
        while (originalHeight/scaleFactor > maxHeight || originalWidth/scaleFactor > maxWidth){
            scaleFactor *= 2;
        }
        if (originalHeight > maxHeight || originalWidth > maxWidth) {
            Bitmap out = Bitmap.createScaledBitmap(bitmap, originalWidth / scaleFactor, originalHeight / scaleFactor, false);

            File file = new File(dir, "resize.jpeg");
            FileOutputStream fOut;

            try {
                fOut = new FileOutputStream(file);
                out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                bitmap.recycle();
                out.recycle();
            } catch (Exception e) {
            }
            return file;
        }
        return photoFile;
    }
}