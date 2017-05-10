package com.example.mingchengzhu.dejaphoto;

import android.content.Context;
import android.os.Environment;
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

    public static void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File stateCodecFile = new File(root, sFileName);
            FileWriter writer = new FileWriter(stateCodecFile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            PrintState(stateCodecFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void PrintState(String fileName){

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
        }

    }

}



