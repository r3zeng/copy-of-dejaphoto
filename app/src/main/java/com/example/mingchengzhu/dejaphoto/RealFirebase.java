package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Created by andymeza on 6/1/17.
 */

public class RealFirebase implements iFirebase {

    private static final String TAG = "RealFirebase";

    private String userID = null;

    public static String emailToFirebaseUserID(String email) {
        return email
                .replace(".", "_dot_")
                .replace("$", "_dollar_")
                .replace("[", "_lsb_")
                .replace("]", "_rsb_")
                .replace("#", "_hash_")
                .replace("/", "_slash_")
                .replace("@", "_at_")
                .toLowerCase();
    }

    public String getUserID() {
        return userID;
    }

    public DatabaseReference createDbRefForImageId(String imageID) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child("images").child(imageID);
    }

    public StorageReference createStorageRefForImage(DejaPhoto photo) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        //From Firebase Example
        String uploadFilename = photo.getId() + photo.getFileExtension();

        return storageRef.child("images").child(uploadFilename);
    }

    public void uploadDejaPhoto(DejaPhoto photo, final OnSuccessListener successListener, final OnFailureListener failureListener) {

        /******************************
         * First upload the file
         ******************************/


        //From Firebase Example

        Uri file = Uri.fromFile(Scaling.scaling(photo));

        StorageReference filenameRef = createStorageRefForImage(photo);
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

        DatabaseReference imageRef = createDbRefForImageId(photo.getId());
        photo.writeToDBRef(imageRef);
    }

    public void downloadDejaPhoto(final String id, final OnSuccessListener successListener, final OnFailureListener failureListener) {
        DatabaseReference imageRef = createDbRefForImageId(id);
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

                StorageReference filenameRef = createStorageRefForImage(photo);
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

    public RealFirebase(String userID) {
        this.userID = userID;
    }

    // added for karma count
    public void displayKCount(final String id, final TextView view){
        DatabaseReference imageRef = createDbRefForImageId(id);
        DatabaseReference myRef = imageRef.child(DejaPhoto.PHOTO_KEY_KCOUNT);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long data = dataSnapshot.getValue(Long.class);
                if(data != null) {
                    view.setText(data.toString());
                }
                else{
                    view.setText("0");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setKCount(final String id, final long count){
        DatabaseReference imageRef = createDbRefForImageId(id);
        DatabaseReference myRef = imageRef.child(DejaPhoto.PHOTO_KEY_KCOUNT);
        myRef.setValue(count);
    }
    //
}