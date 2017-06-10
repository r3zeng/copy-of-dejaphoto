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
    public void setUp() {
        tracker = new Tracker();
        tracker.updateTime();
    }

    @Test
    public void testLocation() {
        Tracker tracker = new Tracker();
        tracker.updateLocation(null);
        assertEquals(tracker.getLocation(), null);
    }

}
