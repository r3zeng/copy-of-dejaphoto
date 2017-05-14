package JUnitTests;

import com.example.mingchengzhu.dejaphoto.AutoSwitch;
import com.example.mingchengzhu.dejaphoto.Tracker;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
/**
 * Created by mingchengzhu on 5/14/17.
 */

public class AutoSwitchJUnitTest {

    int time;
    AutoSwitch autoSwitch;

    @Before
    public void setUp(){
        autoSwitch = new AutoSwitch(null, null, null, 0);
        time = 10;
    }

    @Test
    public void testTimeSetter(){

        autoSwitch.setTime(time);
        assertEquals(time, autoSwitch.getTime());
    }

}
