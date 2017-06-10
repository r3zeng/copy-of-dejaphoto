package com.example.mingchengzhu.dejaphoto;

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

    private HashMap<String, DejaPhoto> data = new HashMap<>();
    private String userID = null;

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
    }

    public void downloadAllPhotos(){}

    @Override
   public void loadFriendsFromDataBase() {}

    @Override
    public void CheckIfFriend(String DataBaseID) {}

    @Override
    public void StartUserUpdateListener() {}

    @Override
    public void addFriend(String Email) {}

    @Override
    public ArrayList<String> getAllMutalFriend() {
        return null;
    }

    // for karma count
    public void displayKCount(final String id, final TextView view){
        view.setText(data.get(id).getKarmaCount());
    }

    public void displayLName(final String id, final TextView view, final EditText editText){
    }

    public void setKCount(final String id, final long count){
    }

    public void setLName(final String id, final String lname){
        data.get(id).setLocationName(lname);
    }

    public void getUserDefined(final String id){
    }
    public void setUserDefined(final String id, final boolean userDefined){

    }
    //

}
