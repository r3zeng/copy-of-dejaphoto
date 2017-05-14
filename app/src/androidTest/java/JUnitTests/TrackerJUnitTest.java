package JUnitTests;
import com.example.mingchengzhu.dejaphoto.Tracker;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by tianhuic on 4/30/17.
 */

public class TrackerJUnitTest {
    Tracker tracker;

    @Before
    public void setUp(){
        tracker = new Tracker();
        tracker.updateTime();
    }

    @Test
    public void testLocation(){
        Tracker tracker = new Tracker();
        tracker.updateLocation(null);
        assertEquals(tracker.getLocation(), null);
    }

    @Test
    public void testGetAndUpdateTime(){
        long time1 = tracker.getTime();
        tracker.updateTime();
        long time2 = tracker.getTime();
        tracker.updateTime();
        long time3 = tracker.getTime();

        assertTrue(time1 > 0);
        assertEquals(time1, time2);
        assertEquals(time2, time3);
    }
}
