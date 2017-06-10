package com.example.mingchengzhu.dejaphoto;

import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
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
import java.util.HashMap;
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

    //same as user friends on server but stored locally for faster access
    private ArrayList<String> friendList;
    private ArrayList<Integer> MutalfriendIndex;


    FirebaseDatabase database;
    DatabaseReference reference;

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
                reference.child("Users").child(userID).child("Update").setValue("true");
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
        ArrayList<String> friendList = getAllMutalFriend();
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

        friendList = new ArrayList<String>();
        MutalfriendIndex = new ArrayList<Integer>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @Override
    public void loadFriendsFromDataBase() {
        Query queryRef = reference.child("Users").child(userID).child("size");

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getValue() != null){
                    int numfriends = Integer.parseInt(snapshot.getValue().toString());
                    loadFriendsFromDataBaseHelper(numfriends);
                }else{
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
       });
    }

    private void loadFriendsFromDataBaseHelper(int numfriends) {
        for (int i = 0; i < numfriends; i++) {
            final int index = i;

            //load all friends
            Query queryRef = reference.child("Users").child(userID).child(i + "");
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot snapshot) {
                   if (snapshot != null && snapshot.getValue() != null) {

                       String value = snapshot.getValue().toString();
                        friendList.add(value);
                   }
               }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG1", "Failed to read value.", error.toException());
                }
            });

            //identify mutual friends based on database info
            Query queryRef2 = reference.child("Users").child(userID).child(i + ":friended you");
            queryRef2.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.getValue() != null) {
                        String value = snapshot.getValue().toString();
                        if (value.equals("true")) {
                            MutalfriendIndex.add(index);
                            downloadImageOfaFriend(friendList.get(MutalfriendIndex.get(index)));
                           // downloadImageOfaFriend(friendList.get(index));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG1", "Failed to read value.", error.toException());
                }
            });
        }
    }

    @Override
    public void CheckIfFriend(String DataBaseID) {
        final String ID = DataBaseID;

        Query queryRef = reference.child("Users").child(DataBaseID).child("size");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getValue() != null){
                    int size = Integer.parseInt(snapshot.getValue().toString());
                    checkIfFriendHelper(ID, size);
                }
            }
            @Override
           public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
    }

    private void checkIfFriendHelper(final String ID, int size){
        for(int i = 0; i < size; i++){
            final int index = i;

            Query queryRef = reference.child("Users").child(ID).child(i + "");
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                 public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.getValue() != null){
                        if(snapshot.getValue().toString().equals(userID)) {
                            MutalfriendIndex.add(friendList.size() - 1);
                            reference.child("Users").child(userID).child(friendList.size() - 1 + ":friended you").setValue("true");
                            reference.child("Users").child(ID).child(index + ":friended you").setValue("true");
                            reference.child("Users").child(ID).child("Update").setValue("true");
                        }
                    }
                }
                 @Override
                 public void onCancelled(DatabaseError error) {
                     // Failed to read value
                     Log.w("TAG1", "Failed to read value.", error.toException());
                 }
            });
        }
    }

     @Override
     public void StartUserUpdateListener() {
         reference.child("Users").child(userID).child("sharing").setValue(true);
         reference.child("Users").orderByChild("Update").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 for (DataSnapshot child: dataSnapshot.getChildren())
                 if(child != null && child.getValue() != null) {
                     if(child.getValue().toString().equals("true")){
                         friendList.clear();
                         MutalfriendIndex.clear();
                         loadFriendsFromDataBase();
                         reference.child("Users").child(child.getKey()).child("Update").setValue("false");
                     }

                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
             }
         });
         /*
         reference.child("Users").child(userID).child("Update").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                     if(dataSnapshot.getValue().toString().equals("true")){
                         friendList.clear();
                         MutalfriendIndex.clear();
                         loadFriendsFromDataBase();
                         reference.child("Users").child(userID).child("Update").setValue("false");
                     }

                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
            }
        });
        */

     }

    @Override
    public void addFriend(String Email) {
        //cant add yourself as friend
        if(emailToFirebaseUserID(Email).equals(userID)){
            return;
        }
        //prevents repeats
        for (int i = 0; i < friendList.size(); i++){
            if(friendList.get(i).equals(emailToFirebaseUserID(Email))){
                return;
            }
        }

        //store locally
        friendList.add(emailToFirebaseUserID(Email));

        //add to server
        reference.child("Users").child(userID).child("size").setValue(friendList.size());
        reference.child("Users").child(userID).child(friendList.size() - 1 + "").setValue(friendList.get(friendList.size() -1));
        reference.child("Users").child(userID).child(friendList.size() - 1 + ":friended you").setValue("false");

        //checks if added friend has already added you and updates database if so
        CheckIfFriend(emailToFirebaseUserID(Email));

    }

    @Override
    public ArrayList<String> getAllMutalFriend() {
        ArrayList<String> ret = new ArrayList<String>();
        for(int i = 0; i < MutalfriendIndex.size(); i++){
            ret.add(friendList.get(MutalfriendIndex.get(i)));
        }
        return  ret;
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

    public void removeAllPhotosOfUser(String user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFirebaseRef = database.getReference("images");

        Query queryRef = myFirebaseRef.orderByChild("pictureOrigin").equalTo(firebaseUserIDToEmail(user));
        Log.i(TAG, "friend email is " + firebaseUserIDToEmail(user));
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null)
                    Log.i(TAG, "No record found");
                else {
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        childSnapshot.getRef().setValue(null);
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

    public void turnOffSharing(){
        reference.child("Users").child(userID).child("sharing").setValue(false);
    }

    public void turnOnSharing(){
        reference.child("Users").child(userID).child("sharing").setValue(true);
    }



}