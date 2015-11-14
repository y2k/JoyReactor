package y2k.joyreactor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by y2k on 11/14/15.
 */
public class HashCodeTest {

    @Test
    public void test() {
        assertEquals(-1778283468, "Alex Gusev".hashCode());
        assertEquals(2062538, "CCCM".hashCode());
        assertEquals(2062538, "Baal".hashCode());
        assertEquals(71940292, "JustP".hashCode());
        assertEquals(71940292, "Jusso".hashCode());
        assertEquals(2605503, "ShУM".hashCode());
        assertEquals(2605503, "Thal".hashCode());
        assertEquals(919234587, "Kkkrevedko".hashCode());
        assertEquals(1234689045, "Mystic girl".hashCode());
    }
}