package com.example.mingchengzhu.dejaphoto;

import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * Created by sterling on 6/1/17.
 */

public interface iFirebase {

    String getUserID();

    void uploadDejaPhoto(DejaPhoto photo, OnSuccessListener successListener, OnFailureListener failureListener);

    void downloadDejaPhoto(final String id, OnSuccessListener successListener, OnFailureListener failureListener);

    void downloadAllFriendsPhotos();

    void loadFriendsFromDataBase();

    void CheckIfFriend(String DataBaseID);

    void StartUserUpdateListener();

    void addFriend(String Email);

    ArrayList<String> getAllMutalFriend();

    // for karma count
    void displayKCount(final String id, final TextView view);

    void displayLName(final String id, final TextView view, final EditText editText);

    void setKCount(final String id, final long count);

    void setLName(final String id, final String lname);
    //
}
