package y2k.joyreactor.services.requests

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.when_
import y2k.joyreactor.requests.MockRequest

/**
 * Created by y2k on 4/26/16.
 */
class TokenRequestTest {

    val mockHttpClient = mock(HttpClient::class.java).apply {
        when_(getText("http://joyreactor.cc/donate")).then { MockRequest.load("token.html") }
    }

    @Test
    fun test() {
        val request = TokenRequest(mockHttpClient)
        val actual = request().get()
        assertEquals(actual, "f374b51bc0fe00c0f6e6159aa28fee3f")
    }
}