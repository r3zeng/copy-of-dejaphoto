package TrackTests;
import com.example.mingchengzhu.dejaphoto.MainActivity;
import com.example.mingchengzhu.dejaphoto.Tracker;
import android.location.Location;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by tianhuic on 4/30/17.
 */

public class TrackJUnitTest {

    @Test
    public void testLocation(){
        Tracker tracker = new Tracker();
        tracker.updateLocation(null);
        assertEquals(tracker.getLocation(), null);
    }

    @Test
    public void testGettingCoordinates(){

    }
}
