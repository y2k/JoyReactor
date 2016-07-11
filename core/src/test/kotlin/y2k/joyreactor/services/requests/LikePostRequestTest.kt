package y2k.joyreactor.services.requests

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import rx.Single
import y2k.joyreactor.common.getResult
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.http.HttpRequestBuilder
import y2k.joyreactor.model.MyLike
import y2k.joyreactor.services.requests.parser.LikeParser

/**
 * Created by y2k on 4/26/16.
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(TokenRequest::class, HttpClient::class, HttpRequestBuilder::class)
class LikePostRequestTest {

    val mockRequestBuilder = mock(HttpRequestBuilder::class.java).apply {
        `when`(get(url)).then { Jsoup.parse(testHtml) }
    }

    val mockHttpClient = mock(HttpClient::class.java).apply {
        `when`(buildRequest()).then { mockRequestBuilder }
    }

    val mockTokenRequest = mock(TokenRequest::class.java).apply {
        `when`(invoke()).then { Single.just("f374b51bc0fe00c0f6e6159aa28fee3f") }
    }

    @Test
    fun test() {
        val request = LikePostRequest(mockHttpClient, mockTokenRequest, LikeParser())
        val actual = request(99, true).getResult()
        assertEquals(13f, actual.first)
        assertEquals(MyLike.Like, actual.second)
        verify(mockRequestBuilder).get(url)
    }
}

const private val url = "http://joyreactor.cc/post_vote/add/99/plus?token=f374b51bc0fe00c0f6e6159aa28fee3f&abyss=0"
const private val testHtml = """<span>13.0<div class="vote-plus "></div>  <div class="vote-minus vote-change"></div></span>"""