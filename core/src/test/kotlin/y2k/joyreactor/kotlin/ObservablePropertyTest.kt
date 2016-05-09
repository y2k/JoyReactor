package y2k.joyreactor.kotlin

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.properties.Delegates

/**
 * Created by y2k on 5/9/16.
 */
class ObservablePropertyTest {

    @Test
    fun test() {
        val testObject = TestClass()
        assertEquals(0, testObject.propCallCount)

        testObject.prop.toString()
        assertEquals(0, testObject.propCallCount)

        testObject.prop = 1
        assertEquals(1, testObject.propCallCount)

        testObject.prop = 1
        assertEquals(2, testObject.propCallCount)
    }

    class TestClass {

        var propCallCount = 0

        var prop: Int by Delegates.observable(0) { p, old, new ->
            propCallCount++

            assertEquals(prop, new)
        }
    }
}