package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Created by andymeza on 6/1/17.
 */

public class RealFirebase implements iFirebase {

    private static final String TAG = "RealFirebase";

    public void uploadDejaPhoto(DejaPhoto photo, final OnSuccessListener successListener, final OnFailureListener failureListener) {

        /******************************
         * First upload the file
         ******************************/

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        //From Firebase Example
        Uri file = Uri.fromFile(photo.getFile());
        String uploadFilename = photo.getId() + photo.getFileExtension();

        StorageReference filenameRef = storageRef.child("images").child(uploadFilename);
        filenameRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                successListener.onSuccess(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                failureListener.onFailure(e);
            }
        });

        /******************************
         * Next update the database
         ******************************/

        // Create a reference to our database
        Location location = photo.getLocation();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference imageRef = database.child("images").child(photo.getId());
        imageRef.child(DejaPhoto.PHOTO_KEY_KCOUNT).setValue(0);
        imageRef.child(DejaPhoto.PHOTO_KEY_LATITUDE).setValue((location == null) ? null : location.getLatitude());
        imageRef.child(DejaPhoto.PHOTO_KEY_LONGITUDE).setValue((location == null) ? null : location.getLatitude());
        imageRef.child(DejaPhoto.PHOTO_KEY_LNAME).setValue(photo.getLocationName());
        imageRef.child(DejaPhoto.PHOTO_KEY_TIME_TAKEN).setValue(photo.getTime());
        imageRef.child(DejaPhoto.PHOTO_KEY_PICTURE_ORIGIN).setValue(photo.getPictureOrigin());
        imageRef.child(DejaPhoto.PHOTO_KEY_FROM_CAMERA).setValue(photo.isFromCamera());
        imageRef.child(DejaPhoto.PHOTO_KEY_FILE_EXT).setValue(photo.getFileExtension());
    }

    public void downloadDejaPhoto(final String id, final OnSuccessListener successListener, final OnFailureListener failureListener) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference imageRef = database.child("images").child(id);
        imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue(false);
                if (value.getClass() != Map.class) {
                    Log.e(TAG, "Firebase query resulted in data in unexpected format - aborting");
                    return;
                }

                Map<String, Object> imageData = (Map<String, Object>)value;
                DejaPhoto photo = new DejaPhoto(imageData, id);

                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();

                //From Firebase Example
                String uploadFilename = id + photo.getFileExtension();

                StorageReference filenameRef = storageRef.child("images").child(uploadFilename);
                filenameRef.getFile(photo.getFile()).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        successListener.onSuccess(taskSnapshot);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        failureListener.onFailure(e);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: pass a better exception
                failureListener.onFailure(new Exception());
            }
        });
    }
}