package com.example.mingchengzhu.dejaphoto;

import android.app.Application;

/**
 * Created by andymeza on 4/30/17.
 */

public class DejaApp extends Application {


    public DejaApp()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        //Album Utility: Checking if dejaAlbum Exists
        if(!AlbumUtility.dejaAlbumExists()) AlbumUtility.createAlbum();
    }

}
