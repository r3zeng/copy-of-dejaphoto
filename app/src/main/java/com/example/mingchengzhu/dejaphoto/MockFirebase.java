package com.example.mingchengzhu.dejaphoto;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by sterling on 6/2/17.
 */

public class MockFirebase implements iFirebase {

    public void uploadDejaPhoto(DejaPhoto photo, OnSuccessListener successListener, OnFailureListener failureListener) {
    }

    public void downloadDejaPhoto(final String id, OnSuccessListener successListener, final OnFailureListener failureListener) {
    }

}
