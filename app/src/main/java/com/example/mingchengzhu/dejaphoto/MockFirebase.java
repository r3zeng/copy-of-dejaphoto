package com.example.mingchengzhu.dejaphoto;

import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * Created by sterling on 6/2/17.
 */

public class MockFirebase implements iFirebase {

    public String getUserID() {
        return "user";
    }

    public void uploadDejaPhoto(DejaPhoto photo, OnSuccessListener successListener, OnFailureListener failureListener) {
    }

    public void downloadDejaPhoto(final String id, OnSuccessListener successListener, final OnFailureListener failureListener) {
    }

    public void downloadAllFriendsPhotos(){}

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
    }

    public void displayLName(final String id, final TextView view, final EditText editText){
    }

    public void setKCount(final String id, final long count){
    }

    public void setLName(final String id, final String lname){
    }

    public void getUserDefined(final String id){
    }
    public void setUserDefined(final String id, final boolean userDefined){
    }
    //

}
