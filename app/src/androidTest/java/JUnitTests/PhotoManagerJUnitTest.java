package JUnitTests;

import com.example.mingchengzhu.dejaphoto.DejaPhoto;
import com.example.mingchengzhu.dejaphoto.PhotoManager;
import com.example.mingchengzhu.dejaphoto.PhotoManagerClient;
import com.example.mingchengzhu.dejaphoto.Tracker;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Andrew on 5/14/2017.
 */

public class PhotoManagerJUnitTest {
    PhotoManagerClient testClient;
    PhotoManager testManager;
    Tracker tracker;
    final long MILISECONDS_IN_DAY = 86400000;
    
    @Before
    public void setUp(){
        testManager = new PhotoManager(testClient);
        testManager.setMatchDate(true);
        testManager.setMatchKarma(true);
        testManager.setMatchLocation(true);
        testManager.setMatchTime(true);
    }

    @Test
    public void testKarmaWeight(){
        DejaPhoto testPhoto1 = new DejaPhoto("user@ucsd.edu", "dummytest", 0, 0, true, false, 1000000);
        DejaPhoto testPhoto2 = new DejaPhoto("user@ucsd.edu", "dummytest2", 0, 0, false, true, 1000000);
        double weight = testManager.getKarmaWeight(testPhoto1);
        assertEquals(4.0, weight, 0.0);
        double weight2 = testManager.getKarmaWeight(testPhoto2);
        assertEquals(1.0, weight2, 0.0);
        testManager.setMatchKarma(false);
        weight = testManager.getKarmaWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);
    }
    @Test
    public void testRelasedWeight() {
        DejaPhoto testPhoto1 = new DejaPhoto("user@ucsd.edu", "dummytest", 0, 0, true, false, 1000000);
        double weight = testManager.getReleasedWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);
        testPhoto1.setReleased(true);
        weight = testManager.getReleasedWeight(testPhoto1);
        assertEquals(0.0, weight, 0.0);
    }
    @Test
    public void testTimeWeight(){
        Tracker tracker = new Tracker();
        DejaPhoto testPhoto1 = new DejaPhoto("user@ucsd.edu", "dummytest", 0, 0, true, false, 1000000);
        double weight = testManager.getTimeWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);
        testPhoto1.setTime(tracker.getTime());
        weight = testManager.getTimeWeight(testPhoto1);
        assertEquals(4.0, weight, 0.0);
        testManager.setMatchTime(false);
        weight = testManager.getTimeWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);

    }
    @Test
    public void testDateWeight(){
        Tracker tracker = new Tracker();
        DejaPhoto testPhoto1 = new DejaPhoto("user@ucsd.edu", "dummytest", 0, 0, true, false, 1000000);
        double weight = testManager.getDateWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);
        testPhoto1.setTime(tracker.getTime());
        weight = testManager.getDateWeight(testPhoto1);
        assertEquals(4.0, weight, 0.0);
        testPhoto1.setTime(tracker.getTime()-MILISECONDS_IN_DAY);
        weight = testManager.getDateWeight(testPhoto1);
        assertEquals(3.4, weight, 0.0);
        testPhoto1.setTime(tracker.getTime()-7*MILISECONDS_IN_DAY);
        weight = testManager.getDateWeight(testPhoto1);
        assertEquals(2.8, weight, 0.0);
        testPhoto1.setTime(tracker.getTime()-30*MILISECONDS_IN_DAY);
        weight = testManager.getDateWeight(testPhoto1);
        assertEquals(2.0, weight, 0.0);
        testPhoto1.setTime(tracker.getTime()-3*30*MILISECONDS_IN_DAY);
        weight = testManager.getDateWeight(testPhoto1);
        assertEquals(1.4, weight, 0.0);
        testManager.setMatchDate(false);
        weight = testManager.getDateWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);
    }
    @Test
    public void testSameDayWeight(){
        long buffer = 100000;
        Tracker tracker = new Tracker();
        DejaPhoto testPhoto1 = new DejaPhoto("user@ucsd.edu", "dummytest", 0, 0, true, false, 0);
        double weight = testManager.getSameDayWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);
        testPhoto1.setTime(tracker.getTime()-7*MILISECONDS_IN_DAY + buffer);
        weight = testManager.getSameDayWeight(testPhoto1);
        assertEquals(4.0, weight, 0.0);
        testPhoto1.setTime(tracker.getTime()- 5*MILISECONDS_IN_DAY + buffer);
        weight = testManager.getSameDayWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);
        testPhoto1.setTime(tracker.getTime()-6*7*MILISECONDS_IN_DAY + buffer);
        weight = testManager.getSameDayWeight(testPhoto1);
        assertEquals(4.0, weight, 0.0);
        testManager.setMatchDate(false);
        weight = testManager.getSameDayWeight(testPhoto1);
        assertEquals(1.0, weight, 0.0);
    }

    @Test
    public void testLocationWeight(){
        // very difficult to test through junit testing
        // tested through other, mostly qualitative, means
    }
    @Test
    public void testRecentWeight(){
        // tested in PreviousImage testing
    }
    @Test
    public void testTotalWeight() {
        // this method is simply multiplication. because all the factors in the multiplication
        // were tested we decided not to test it.

    }

}
