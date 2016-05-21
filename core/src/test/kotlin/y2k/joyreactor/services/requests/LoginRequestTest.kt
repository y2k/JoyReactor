package y2k.joyreactor.services.requests

import org.jsoup.Jsoup
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.requests.MockRequest

/**
 * Created by y2k on 5/21/16.
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(HttpClient::class)
class LoginRequestTest {

//    val mockResponse = PowerMockito.mock(Response::class.java).apply {
//
//    }

    val mockHttpClient = PowerMockito.mock(HttpClient::class.java).apply {
        `when`(getDocument("http://joyreactor.cc/login"))
            .then { Jsoup.parse(MockRequest.load("login.html")) }

//        `when`(executeRequest(anyString(), any(), any()))
//            .then { null }
    }

    @Test
    fun test() {
        val request = LoginRequestFactory(mockHttpClient)
        request.request("user500", "password").toBlocking().last();
    }
}