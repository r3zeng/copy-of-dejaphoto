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

    /**
     * Uploads a dejaphoto to Firebase database
     * @param photo
     * @param successListener
     * @param failureListener
     */
    void uploadDejaPhoto(DejaPhoto photo, OnSuccessListener successListener, OnFailureListener failureListener);

    /**
     * Downloads a dejaphoto with all of its fields
     * @param id
     * @param successListener
     * @param failureListener
     */
    void downloadDejaPhoto(final String id, OnSuccessListener successListener, OnFailureListener failureListener);

    /**
     * Downloads all photos of a user including friends
     */
    void downloadAllPhotos();

    /**
     * Loads a list of friends from the database for a user
     */
    void loadFriendsFromDataBase();

    /**
     * Checks if someone is a friend in firebase
     * @param DataBaseID
     */
    void CheckIfFriend(String DataBaseID);

    /**
     * listener for user changes
     */
    void StartUserUpdateListener();

    /**
     * adds a friend field to a user on firebase
     * @param Email
     */
    void addFriend(String Email);

    /**
     * list of mutual friends
     * @return
     */
    ArrayList<String> getAllMutalFriend();

    /**
     * total karma count among all users
     * @param id
     * @param view
     */
    void displayKCount(final String id, final TextView view);

    /**
     * displays location name from firebase
     * @param id
     * @param view
     * @param editText
     */
    void displayLName(final String id, final TextView view, final EditText editText);

    /**
     * updates kcount for an image in firebase
     * @param id
     * @param count
     */
    void setKCount(final String id, final long count);

    /**
     * updates an images location name
     * @param id
     * @param lname
     */
    void setLName(final String id, final String lname);
}
