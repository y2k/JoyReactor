package y2k.joyreactor.services

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import rx.Single
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.Notifications
import y2k.joyreactor.common.subscribe

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

    @Test
    fun testLifeCycleService() {
        val single = Single.fromCallable { "" } to Notifications.Posts
        val lifeCycleService = LifeCycleService(BroadcastService)

        var count = 0
        single.subscribe(lifeCycleService) { count++ }

        assertEquals(1, count)

        BroadcastService.broadcast(Notifications.Posts)
        assertEquals(1, count)

        lifeCycleService.activate()
        assertEquals(2, count)

        BroadcastService.broadcast(Notifications.Posts)
        assertEquals(3, count)
        BroadcastService.broadcast(Notifications.Posts)
        assertEquals(4, count)

        lifeCycleService.deactivate()
        assertEquals(4, count)

        BroadcastService.broadcast(Notifications.Posts)
        assertEquals(4, count)
    }
}