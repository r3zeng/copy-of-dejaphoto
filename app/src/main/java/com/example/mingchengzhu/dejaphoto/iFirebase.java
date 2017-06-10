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
     *
     * @param photo
     * @param successListener
     * @param failureListener
     */
    void uploadDejaPhoto(DejaPhoto photo, OnSuccessListener successListener, OnFailureListener failureListener);

    /**
     *
     * @param id
     * @param successListener
     * @param failureListener
     */
    void downloadDejaPhoto(final String id, OnSuccessListener successListener, OnFailureListener failureListener);

    /**
     *
     */
    void downloadAllPhotos();

    /**
     *
     */
    void loadFriendsFromDataBase();

    /**
     *
     * @param DataBaseID
     */
    void CheckIfFriend(String DataBaseID);

    /**
     *
     */
    void StartUserUpdateListener();

    /**
     *
     * @param Email
     */
    void addFriend(String Email);

    /**
     *
     * @return
     */
    ArrayList<String> getAllMutalFriend();

    /**
     *
     * @param id
     * @param view
     */
    void displayKCount(final String id, final TextView view);

    /**
     *
     * @param id
     * @param view
     * @param editText
     */
    void displayLName(final String id, final TextView view, final EditText editText);

    /**
     *
     * @param id
     * @param count
     */
    void setKCount(final String id, final long count);

    /**
     *
     * @param id
     * @param lname
     */
    void setLName(final String id, final String lname);
}
