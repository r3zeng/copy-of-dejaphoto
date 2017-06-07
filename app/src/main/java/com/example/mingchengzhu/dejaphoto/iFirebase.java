package com.example.mingchengzhu.dejaphoto;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by sterling on 6/1/17.
 */

public interface iFirebase {

    void uploadDejaPhoto(DejaPhoto photo, OnSuccessListener successListener, OnFailureListener failureListener);

    void downloadDejaPhoto(final String id, OnSuccessListener successListener, OnFailureListener failureListener);

}
