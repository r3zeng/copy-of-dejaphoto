package TrackTests;
import com.example.mingchengzhu.dejaphoto.Tracker;
import android.location.Location;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by tianhuic on 4/30/17.
 */

public class TrackJUnitTest {

    @Test
    public void testCompareTime(){
        Tracker tracker = new Tracker();
        tracker.set(1000000, null);
        assertEquals(tracker.compareTime(), true);
    }

    @Test
    public void testCompareLocation(){}

    @Test
    public void testCompare(){}
}
