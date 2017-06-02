package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by andymeza on 6/1/17.
 */

public class RealFirebase implements iFirebase {

    private static final String PHOTO_KEY_KCOUNT = "karmaCount";
    private static final String PHOTO_KEY_LATITUDE = "latitude";
    private static final String PHOTO_KEY_LONGITUDE = "longitude";
    private static final String PHOTO_KEY_LNAME = "locationName";
    private static final String PHOTO_KEY_TIME_TAKEN = "timeTaken";


    public void uploadDejaPhoto(DejaPhoto photo, OnSuccessListener successListener, OnFailureListener failureListener) {

        /******************************
         * First upload the file
         ******************************/

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        //From Firebase Example
        Uri file = photo.getUri();
        StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        /******************************
         * Next update the database
         ******************************/

        DatabaseReference database;

        // Create a reference to our database
        Location location = photo.getLocation();

        database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference imageRef = database.child("images").child(file.getLastPathSegment());
        imageRef.child(PHOTO_KEY_KCOUNT).setValue(0);
        imageRef.child(PHOTO_KEY_LATITUDE).setValue((location == null) ? null : location.getLatitude());
        imageRef.child(PHOTO_KEY_LONGITUDE).setValue((location == null) ? null : location.getLatitude());
        imageRef.child(PHOTO_KEY_LNAME).setValue(photo.getLocationName());
        imageRef.child(PHOTO_KEY_TIME_TAKEN).setValue(photo.getTime());
    }

    public DejaPhoto downloadDejaPhoto(String pathName, OnSuccessListener successListener, OnFailureListener failureListener) {
        return null;

    }
}