package com.example.mingchengzhu.dejaphoto;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sterling on 6/2/17.
 */

public class MockFirebase implements iFirebase {

    private final static String TAG = "MockFirebase";

    private ArrayList<String> mutualFriends = new ArrayList<String>();
    private HashMap<String, DejaPhoto> data = new HashMap<>();
    private String userID = "1234";

    public HashMap<String, DejaPhoto> getData(){
        return data;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void uploadDejaPhoto(DejaPhoto photo, OnSuccessListener successListener, OnFailureListener failureListener) {
        data.put(photo.getId(), photo);
    }

    public void downloadDejaPhoto(final String id, OnSuccessListener successListener, final OnFailureListener failureListener) {
        successListener.onSuccess(new Object());
    }

    public void downloadAllPhotos(){
        Log.d(TAG, "mock downloadAllPhotos called");
    }

    @Override
   public void loadFriendsFromDataBase() {
        Log.d(TAG, "mock loadFriendsFromDataBase called");
    }

    @Override
    public void CheckIfFriend(String DataBaseID) {
        Log.d(TAG, "mock CheckIfFriend called");
    }

    @Override
    public void StartUserUpdateListener() {
        Log.d(TAG, "mock StartUserUpdateListener called");
    }

    @Override
    public void addFriend(String Email) {
        if (Email.substring(0, 1).equalsIgnoreCase("a")) {
            mutualFriends.add(Email);
        }
    }

    @Override
    public ArrayList<String> getAllMutalFriend() {
        return mutualFriends;
    }

    // for karma count
    public void displayKCount(final String id, final TextView view){
        view.setText(data.get(id).getKarmaCount());
    }

    public void displayLName(final String id, final TextView view, final EditText editText){
        view.setText("Mock Location Name!");
        editText.setText("Mock Location Name!");
    }

    public void setKCount(final String id, final long count){
        Log.d(TAG, "mock setKCount called");
    }

    public void setLName(final String id, final String lname){
        data.get(id).setLocationName(lname);
    }
    //

}
