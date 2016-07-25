package y2k.joyreactor.services.requests

import org.junit.Test
import org.mockito.Mockito.*
import org.powermock.core.classloader.annotations.PrepareForTest
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.http.RequestBuilder
import y2k.joyreactor.common.when_
import y2k.joyreactor.requests.MockRequest

/**
 * Created by y2k on 5/21/16.
 */
@PrepareForTest(HttpClient::class)
class LoginRequestTest {

    val mockRequestBuilder = mock(RequestBuilder::class.java).apply {
        when_(addField(anyString(), anyString())).then { it.mock }

        when_(post(anyString()))
            .then { MockRequest.loadDocument("login.response.html") }
    }

    val mockHttpClient = mock(HttpClient::class.java).apply {
        when_(getDocument("http://joyreactor.cc/login"))
            .then { MockRequest.loadDocument("login.html") }

        when_(buildRequest())
            .then { mockRequestBuilder }
    }

    @Test
    fun test() {
        val request = LoginRequestFactory(mockHttpClient)
        request.request("user500", "password").get()

        verify(mockRequestBuilder).addField("signin[username]", "user500")
        verify(mockRequestBuilder).addField("signin[password]", "password")
        verify(mockRequestBuilder).addField("signin[_csrf_token]", "cc037c7f3b0169ec300d6b8d666660fb")
        verify(mockRequestBuilder, times(3)).addField(anyString(), anyString())
    }
}