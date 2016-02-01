package y2k.joyreactor

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by y2k on 11/14/15.
 */
class HashCodeTest {

    @Test
    fun test() {
        assertEquals(-1778283468, "Alex Gusev".hashCode().toLong())
        assertEquals(2062538, "CCCM".hashCode().toLong())
        assertEquals(2062538, "Baal".hashCode().toLong())
        assertEquals(71940292, "JustP".hashCode().toLong())
        assertEquals(71940292, "Jusso".hashCode().toLong())
        assertEquals(2605503, "ShУM".hashCode().toLong())
        assertEquals(2605503, "Thal".hashCode().toLong())
        assertEquals(919234587, "Kkkrevedko".hashCode().toLong())
        assertEquals(1234689045, "Mystic girl".hashCode().toLong())
    }
}