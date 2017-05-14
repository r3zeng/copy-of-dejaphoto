package com.example.mingchengzhu.dejaphoto;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by andymeza on 5/8/17.
 */

public class StateCodec {

    private static final String TAG = "StateCodec";

    public static void addDejaPhotoToSC(Context context, String sFileName, DejaPhoto dejaP)
    {
        Log.d(TAG, "Begin DejaPhotoToSC");
        if (dejaP.getSavedToFile())
        {
            return;
        }

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            String sBody = dejaP.toString(context);
            File stateCodecFile = new File(root, sFileName);
            FileWriter writer = new FileWriter(stateCodecFile,true);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            printState(stateCodecFile.getAbsolutePath());
            Log.i(TAG, "DejaPhoto should now be saved to a file");
        } catch (IOException e) {
            Log.d(TAG, "Exception occurs under addDejaPhotoToSC method");
            e.printStackTrace();
        }
    }

    public static void printState(String fileName)
    {

        File currState = new File(fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(currState));
            String line;
            while ((line = br.readLine()) != null)
            {
                System.out.println(line);

            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            Log.d(TAG, "Exception occurs under printState method");
        }

    }

    public static void loadState(Context context, String fileName)
    {

        File currState = new File(fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(currState));
            String line;
            while ((line = br.readLine()) != null)
            {
                int counter = 0;
                DejaPhoto dejaPhoto = null;

                switch(counter%4)
                {
                    case 0:
                        Uri fromPath = Uri.fromFile(new File(line));
                        dejaPhoto = new DejaPhoto(fromPath, context);
                        break;
                    case 1:
                        if(line.contains("false"))
                        {
                            dejaPhoto.setKarma(false);
                        }
                        else
                        {
                            dejaPhoto.setKarma(true);
                        }
                        break;
                    case 2:
                        if(line.contains("false"))
                        {
                            dejaPhoto.setReleased(false);
                        }
                        else
                        {
                            dejaPhoto.setReleased(true);
                        }
                        break;
                    case 3:
                        dejaPhoto.setTime((long)Integer.parseInt(line));

                }


            }
            br.close();
            Log.i(TAG, "The state should have been loaded");
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            Log.d(TAG, "Exception occurs under loadState method");
        }
    }

}



