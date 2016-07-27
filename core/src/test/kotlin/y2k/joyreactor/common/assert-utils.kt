package y2k.joyreactor.common

import org.junit.Assert

/**
 * Created by y2k on 26/07/16.
 */

fun assertTrueTimeout(actual: () -> Boolean) {
    for (i in 0..7) {
        if (actual()) return
        Thread.sleep(10L shl i)
    }
    Assert.fail()
}