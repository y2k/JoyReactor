package y2k.joyreactor.services

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.ChangePostFavoriteRequest
import y2k.joyreactor.services.requests.LikePostRequest
import y2k.joyreactor.services.requests.OriginalImageRequestFactory
import y2k.joyreactor.services.requests.PostRequest

/**
 * Created by y2k on 27/06/16.
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(
    OriginalImageRequestFactory::class,
    MemoryBuffer::class,
    Entities::class,
    LikePostRequest::class,
    Platform::class,
    BroadcastService::class,
    ChangePostFavoriteRequest::class)
class PostServiceTests {

    @Test
    fun test() {
        ForegroundScheduler.instance = Schedulers.immediate()

        val service = PostService(
            mock(OriginalImageRequestFactory::class.java),
            { Observable.empty() },
            mock(Entities::class.java),
            mock(LikePostRequest::class.java),
            mock(Platform::class.java),
            mock(BroadcastService::class.java),
            mock(ChangePostFavoriteRequest::class.java))

        val actual = service.mainImageFromDisk(2686902).toBlocking().value()

        assertNotNull(actual.result)
        assertTrue("file = " + actual.result, actual.result!!.endsWith(".jpg"))
    }
}