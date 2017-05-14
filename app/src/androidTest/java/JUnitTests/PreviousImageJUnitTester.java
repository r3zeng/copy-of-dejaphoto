package JUnitTests;

import com.example.mingchengzhu.dejaphoto.DejaPhoto;
import com.example.mingchengzhu.dejaphoto.PreviousImage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PreviousImageJUnitTester {

@Test
    public void TestRightSwipe(){

        PreviousImage PI = new PreviousImage();
        int index_at_start = PI.getNumberofPhoto();

        DejaPhoto DummyPhoto1 = new DejaPhoto("picture 1", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto2 = new DejaPhoto("picture 2", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto3 = new DejaPhoto("picture 3", 0, 0, false, false, 0);

        PI.swipeRight(DummyPhoto1);
        PI.swipeRight(DummyPhoto2);
        PI.swipeRight(DummyPhoto3);

        int index_at_end = PI.getNumberofPhoto();


        assertEquals(index_at_start, 0);
        assertEquals(index_at_end, 3);
    }

    @Test
    public void TestLeftSwipe(){
        PreviousImage PI = new PreviousImage();
        int index_at_start = PI.getNumberofPhoto();

        DejaPhoto DummyPhoto1 = new DejaPhoto("picture 1", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto2 = new DejaPhoto("picture 2", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto3 = new DejaPhoto("picture 3", 0, 0, false, false, 0);

        PI.swipeRight(DummyPhoto1);//current photo is now photo 1
        PI.swipeRight(DummyPhoto2);//current photo is now photo 2
        PI.swipeRight(DummyPhoto3);//current photo is now photo 3

        DejaPhoto pop1 = PI.swipeLeft();//prev photo is photo 2
        DejaPhoto pop2 = PI.swipeLeft();//prev photo is photo 1
        DejaPhoto pop3 = PI.swipeLeft();//no prev photo (returns null)

        int index_at_end = PI.getNumberofPhoto();

        assertEquals(index_at_start, 0);
        assertEquals(index_at_end, 1);
        assertEquals(DummyPhoto2, pop1);
        assertEquals(DummyPhoto1, pop2);
        assertEquals(null, pop3);

    }

    @Test
    public void RightThenLeft(){
        PreviousImage PI = new PreviousImage();
        int index_at_start = PI.getNumberofPhoto();

        DejaPhoto DummyPhoto1 = new DejaPhoto("picture 1", 0, 0, false, false, 0);
        PI.swipeRight(DummyPhoto1);
        DejaPhoto pop = PI.swipeLeft(); //no prev image (return null)

        int index_at_end = PI.getNumberofPhoto();

        assertEquals(index_at_start, 0);
        assertEquals(index_at_end, 1);
        assertEquals(pop, null);

    }

    @Test
    public void TestLastImage(){
        PreviousImage PI = new PreviousImage();
        int index_at_start = PI.getNumberofPhoto();

        DejaPhoto DummyPhoto1 = new DejaPhoto("picture 1", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto2 = new DejaPhoto("picture 2", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto3 = new DejaPhoto("picture 3", 0, 0, false, false, 0);

        //no photo
        DejaPhoto curr1 = PI.getCurrentPhoto();
        PI.swipeRight(DummyPhoto1);//current photo 1
        DejaPhoto curr2 = PI.getCurrentPhoto();
        PI.swipeRight(DummyPhoto2);//current photo 2
        DejaPhoto curr3 = PI.getCurrentPhoto();
        PI.swipeRight(DummyPhoto3);//current photo 3
        DejaPhoto curr4 = PI.getCurrentPhoto();

        int index_at_end = PI.getNumberofPhoto();

        assertEquals(index_at_start, 0);
        assertEquals(index_at_end, 3);
        assertEquals(curr1, null);
        assertEquals(curr2, DummyPhoto1);
        assertEquals(curr3, DummyPhoto2);
        assertEquals(curr4, DummyPhoto3);
    }
    
    @Test
    public void TestBackAndForth(){
        PreviousImage PI = new PreviousImage();
        int index_at_start = PI.getNumberofPhoto();

        DejaPhoto DummyPhoto1 = new DejaPhoto("picture 1", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto2 = new DejaPhoto("picture 2", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto3 = new DejaPhoto("picture 3", 0, 0, false, false, 0);

        PI.swipeRight(DummyPhoto1);
        PI.swipeRight(DummyPhoto2);
        DejaPhoto prev1 = PI.swipeLeft();
        PI.swipeRight(DummyPhoto3);
        DejaPhoto prev2 = PI.swipeLeft();
        PI.swipeRight(DummyPhoto2);
        DejaPhoto prev3 = PI.swipeLeft();

        int index_at_end = PI.getNumberofPhoto();

        assertEquals(index_at_start, 0);
        assertEquals(index_at_end, 1);
        assertEquals(prev1, DummyPhoto1);
        assertEquals(prev2, DummyPhoto1);
        assertEquals(prev3, DummyPhoto1);
    }
    
    @Test
    public void TestOverflowingBuffer(){
        PreviousImage PI = new PreviousImage();
        int index_at_start = PI.getNumberofPhoto();

        DejaPhoto DummyPhoto1 = new DejaPhoto("picture 1", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto2 = new DejaPhoto("picture 2", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto3 = new DejaPhoto("picture 3", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto4 = new DejaPhoto("picture 4", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto5 = new DejaPhoto("picture 5", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto6 = new DejaPhoto("picture 6", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto7 = new DejaPhoto("picture 7", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto8 = new DejaPhoto("picture 8", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto9 = new DejaPhoto("picture 9", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto10 = new DejaPhoto("picture 10", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto11 = new DejaPhoto("picture 11", 0, 0, false, false, 0);
        DejaPhoto DummyPhoto12 = new DejaPhoto("picture 12", 0, 0, false, false, 0);

        PI.swipeRight(DummyPhoto1);//overwritten
        PI.swipeRight(DummyPhoto2);//pre 10
        PI.swipeRight(DummyPhoto3);//pre 9
        PI.swipeRight(DummyPhoto4);//pre 8
        PI.swipeRight(DummyPhoto5);//pre 7
        PI.swipeRight(DummyPhoto6);//pre 6
        PI.swipeRight(DummyPhoto7);//pre 5
        PI.swipeRight(DummyPhoto8);//pre 4
        PI.swipeRight(DummyPhoto9);//pre 3
        PI.swipeRight(DummyPhoto10);//pre 2
        PI.swipeRight(DummyPhoto11);//pre 1
        PI.swipeRight(DummyPhoto12);//curr

        DejaPhoto pre1 = PI.swipeLeft();
        DejaPhoto pre2 = PI.swipeLeft();
        DejaPhoto pre3 = PI.swipeLeft();
        DejaPhoto pre4 = PI.swipeLeft();
        DejaPhoto pre5 = PI.swipeLeft();
        DejaPhoto pre6 = PI.swipeLeft();
        DejaPhoto pre7 = PI.swipeLeft();
        DejaPhoto pre8 = PI.swipeLeft();
        DejaPhoto pre9 = PI.swipeLeft();
        DejaPhoto pre10 = PI.swipeLeft();
        DejaPhoto pre11 = PI.swipeLeft();

        int index_at_end = PI.getNumberofPhoto();

        assertEquals(index_at_start, 0);
        assertEquals(index_at_end, 1);
        assertEquals(pre1, DummyPhoto11);
        assertEquals(pre2, DummyPhoto10);
        assertEquals(pre3, DummyPhoto9);
        assertEquals(pre4, DummyPhoto8);
        assertEquals(pre5, DummyPhoto7);
        assertEquals(pre6, DummyPhoto6);
        assertEquals(pre7, DummyPhoto5);
        assertEquals(pre8, DummyPhoto4);
        assertEquals(pre9, DummyPhoto3);
        assertEquals(pre10, DummyPhoto2);
        assertEquals(pre11, null);
    }
    
    @Test
    public void TestPhotoSeen(){
        PreviousImage PI = new PreviousImage();
        int index_at_start = PI.getNumberofPhoto();

        DejaPhoto DummyPhoto1 = new DejaPhoto("picture 1", 0, 0, false, false, 0);

        boolean seen1 = PI.PhotoPreviouslySeen(DummyPhoto1);//false

        PI.swipeRight(DummyPhoto1);

        boolean seen2 = PI.PhotoPreviouslySeen(DummyPhoto1);//true

        int index_at_end = PI.getNumberofPhoto();

        assertEquals(index_at_start, 0);
        assertEquals(index_at_end, 1);
        assertEquals(seen1, false);
        assertEquals(seen2, true);

    }
}
