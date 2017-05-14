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
        assertEquals(index_at_end, 0);
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
        DejaPhoto prev1 = PI.getCurrentPhoto();
        PI.swipeRight(DummyPhoto1);//current photo 1
        DejaPhoto prev2 = PI.getCurrentPhoto();
        PI.swipeRight(DummyPhoto2);//current photo 2
        DejaPhoto prev3 = PI.getCurrentPhoto();
        PI.swipeRight(DummyPhoto3);//current photo 3
        DejaPhoto prev4 = PI.getCurrentPhoto();

        int index_at_end = PI.getNumberofPhoto();

        assertEquals(index_at_start, 0);
        assertEquals(index_at_end, 3);
        assertEquals(prev1, null);
        assertEquals(prev2, DummyPhoto1);
        assertEquals(prev3, DummyPhoto2);
        assertEquals(prev4, DummyPhoto3);
    }
}
