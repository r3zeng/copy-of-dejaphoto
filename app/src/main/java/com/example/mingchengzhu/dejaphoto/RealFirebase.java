package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * Created by andymeza on 6/1/17.
 */

public class RealFirebase implements iFirebase {

    private static final String TAG = "RealFirebase";

    private String userID = null;

    private PhotoManager photoManager;

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

    public static String firebaseUserIDToEmail(String userID) {
        return userID
                .replace("_dot_", ".")
                .replace("_dollar_","$")
                .replace("_lsb_","[")
                .replace("_rsb_","]")
                .replace("_hash_","#")
                .replace("_slash_","/")
                .replace("_at_","@")
                .toLowerCase();
    }

    public String getUserID() {
        return userID;
    }

    public void setPhotoManager(PhotoManager photoManager){
        this.photoManager = photoManager;
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
            Log.i(TAG, "begin downloading a photo");

            DatabaseReference imageRef = createDbRefForImageId(id);
            imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                 /*
                Object value = dataSnapshot.getValue(false);

                if (value.getClass() != Map.class) {
                    Log.e(TAG, "Firebase query resulted in data in unexpected format - aborting");
                    return;
                }

                Map<String, Object> imageData = (Map<String, Object>)value;
                */
                    DownloadedImage imageData = dataSnapshot.getValue(DownloadedImage.class);
                    imageData.setId(id);
                    final DejaPhoto photo = imageData.createDejaPhoto();
                    //final DejaPhoto photo = new DejaPhoto(imageData, id);

                    StorageReference filenameRef = createStorageRefForImage(photo);
                    filenameRef.getFile(photo.getFile()).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            successListener.onSuccess(taskSnapshot);
                            photoManager.addPhoto(photo);
                           // photoManager.setCurrentPhoto(photo);
                            Log.i(TAG, "photo should be set to wallpaper now");
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

    public void downloadAllFriendsPhotos(){
        ArrayList<String> friendList = MainActivity.getAllMutalFriend();
        if (friendList.size() == 0) {
            Log.i(TAG, "the user does not have any friend");
            return;
        }

        for (int i = 0; i < friendList.size(); i++){
            String friend = friendList.get(i);
            downloadImageOfaFriend(friend);
        }
    }

    public void downloadImageOfaFriend(String friend){
        Log.i(TAG, friend + "'s photo should be downloaded");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFirebaseRef = database.getReference("images");
        Log.i(TAG, "frind is " + friend);
        Query queryRef = myFirebaseRef.orderByChild("pictureOrigin").equalTo(firebaseUserIDToEmail(friend));
        Log.i(TAG, "friend email is " + firebaseUserIDToEmail(friend));
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null)
                    Log.i(TAG, "No record found");
                else {
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        String key = childSnapshot.getKey();
                        Log.i(TAG, "key is" + key);
                        downloadDejaPhoto(key, new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {

                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
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