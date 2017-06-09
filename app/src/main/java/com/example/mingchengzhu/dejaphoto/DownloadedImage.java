package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.util.Log;

/**
 * Created by tianhuic on 6/8/17.
 */

public class DownloadedImage {
    public String fileExtension;
    public boolean isFromCamera;
    public int karmaCount;
    public float latitude;
    public float longitude;
    public boolean hasCoords;
    public String pictureOrigin;
    public long timeTaken;
    public String locationName;
    public String id;

    DownloadedImage(){}

    public DejaPhoto createDejaPhoto(){
        if (hasCoords) {
            return new DejaPhoto(id, fileExtension, isFromCamera, karmaCount, (Double)(double)latitude, (Double)(double)longitude, pictureOrigin, timeTaken, locationName);
        } else {
            return new DejaPhoto(id, fileExtension, isFromCamera, karmaCount, null, null, pictureOrigin, timeTaken, locationName);
        }
    }

    void setId(String id){
        this.id = id;
      /*  if (fileExtension != null){
            this.id = id.substring(0, id.length() - (fileExtension.length()-1));
        }*/
        Log.i("DownloadedImage", "id is"+ this.id);
    }
}
