/*
 * DejaPhotoJUnitTest
 * Unit tests for the DejaPhoto class
 * Created by sterling on 5/14/17.
 */

package JUnitTests;

import com.example.mingchengzhu.dejaphoto.DejaPhoto;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DejaPhotoJUnitTest {

    double[][] coords = {{122, 21}, {-92, 81}, {-92, 82}};
    private DejaPhoto photo1, photo2, photo1Imposter, photoNoLocation;
    private DejaPhoto[] locationPhotos;

    @Before
    public void setUp() {
        // initialize our private member variables

        photo1 = new DejaPhoto("content://photo1", coords[0][0], coords[0][1], false, false, System.currentTimeMillis());
        photo2 = new DejaPhoto("content://photo2", coords[1][0], coords[1][1], false, true, System.currentTimeMillis());
        photo1Imposter = new DejaPhoto("content://photo1", coords[2][0], coords[2][1], false, true, System.currentTimeMillis());
        photoNoLocation = new DejaPhoto("content://noLocation", true, false, System.currentTimeMillis());

        locationPhotos = new DejaPhoto[]{photo1, photo2, photo1Imposter};
    }

    /**
     * Test that the equals() method functions correctly
     */
    @Test
    public void testEquals() {
        assertTrue(photo1.equals(photo1Imposter));
        assertFalse(photo1.equals(photo2));
        assertFalse(photo1Imposter.equals(photo2));
        assertFalse(photo1.equals(null));
    }

    /**
     * Test that DejaPhoto's location field is initialized correctly
     */
    @Test
    public void testLocation() {
        assertTrue(photoNoLocation.getLocation() == null);  // expect no location

        // expect a location on these
        assertTrue(photo1.getLocation() != null);
        assertTrue(photo2.getLocation() != null);
        assertTrue(photo1Imposter.getLocation() != null);

        // check that the coordinates match
        for (int i = 0; i <= 2; ++i) {
            assertEquals(locationPhotos[i].getLocation().getLatitude(), coords[i][0], 0.001);
            assertEquals(locationPhotos[i].getLocation().getLongitude(), coords[i][1], 0.001);
        }
    }

    /*@Test
    public void testToString() {
        for (DejaPhoto p : locationPhotos) {
            String s = p.toString(Context.getApplicationContext());
            assertTrue(s != null && s.length() > 0);
        }
    }*/
}
