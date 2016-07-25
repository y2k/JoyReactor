package y2k.joyreactor.usecase

import org.junit.Before
import org.junit.Test
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.requests.MockHttpClient
import y2k.joyreactor.viewmodel.MainViewModel

/**
 * Created by y2k on 25/07/16.
 */
class OpenMainPage {

    @Before
    fun setUp() {
        ServiceLocator.registerSingleton<Platform> { MockPlatform() }
        ServiceLocator.register<HttpClient> { MockHttpClient() }
    }

    @Test
    fun test() {
        val vm = ServiceLocator.resolve<MainViewModel>()
    }
}