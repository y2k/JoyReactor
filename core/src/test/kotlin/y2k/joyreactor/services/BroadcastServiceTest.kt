package y2k.joyreactor.services

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.Notifications

/**
 * Created by y2k on 5/2/16.
 */
class BroadcastServiceTest {

    @Before
    fun setUp() {
        ForegroundScheduler.instance = Schedulers.immediate()
    }

    @Test
    fun testRegister() {
        val receiver = Any()

        var callCount = 0
        BroadcastService.register<Any>(receiver, Notifications.Posts) {
            callCount++
        }
        assertEquals(0, callCount)
        BroadcastService.broadcast(Notifications.Posts)
        assertEquals(1, callCount)

        BroadcastService.unregisterToken(Notifications.Posts)
        BroadcastService.broadcast(Notifications.Posts)
        assertEquals(1, callCount)
    }
}