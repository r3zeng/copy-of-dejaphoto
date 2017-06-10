package JUnitTests;

import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mingchengzhu.dejaphoto.DejaPhoto;
import com.example.mingchengzhu.dejaphoto.MockFirebase;
import com.example.mingchengzhu.dejaphoto.iFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.junit.Before;
import org.junit.Test;

import java.util.IllegalFormatCodePointException;
import static org.junit.Assert.assertEquals;

/**
 * Created by tianhuic on 6/9/17.
 */

public class FirebaseJUnitTest {

    private iFirebase firebase;
    @Before
    public void setup(){
        firebase = new MockFirebase();

    }
    @Test
    public void testUploading(){
        DejaPhoto dejaPhoto = new
                DejaPhoto("Test", ".jpg", true, 0, 0.0, 0.0, "null@gmail.com", 0, "location");
        firebase.uploadDejaPhoto(dejaPhoto, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        assertEquals(true, ((MockFirebase)firebase).getData().containsValue(dejaPhoto));
        assertEquals(true, ((MockFirebase)firebase).getData().containsKey("Test"));

        DejaPhoto dejaPhoto2 = new
                DejaPhoto("Test2", ".png", false, 0, 10.0, 10.0, "A@gmail.com", 0, "location2");
        firebase.uploadDejaPhoto(dejaPhoto2, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        assertEquals(true, ((MockFirebase)firebase).getData().containsValue(dejaPhoto2));
        assertEquals(true, ((MockFirebase)firebase).getData().containsKey("Test2"));
    }

    @Test
    public void testDisplayCount(){
        DejaPhoto dejaPhoto = new
                DejaPhoto("Test", ".jpg", true, 0, 0.0, 0.0, "null@gmail.com", 0, "location");
        firebase.uploadDejaPhoto(dejaPhoto, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        assertEquals(0, ((MockFirebase)firebase).getData().get("Test").getKarmaCount());
    }

    @Test
    public void testDisplayLName(){
        DejaPhoto dejaPhoto = new
                DejaPhoto("Test", ".jpg", true, 0, 0.0, 0.0, "null@gmail.com", 0, "location");
        firebase.uploadDejaPhoto(dejaPhoto, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        assertEquals("location", ((MockFirebase)firebase).getData().get("Test").getLocationName());
    }

    @Test
    public void testSetLName(){
        DejaPhoto dejaPhoto = new
                DejaPhoto("Test", ".jpg", true, 0, 0.0, 0.0, "null@gmail.com", 0, "location");
        firebase.uploadDejaPhoto(dejaPhoto, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        firebase.setLName("Test", "location2");
        assertEquals("location2", ((MockFirebase)firebase).getData().get("Test").getLocationName());
    }

}
