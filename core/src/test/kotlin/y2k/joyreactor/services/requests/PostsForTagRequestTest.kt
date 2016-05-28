package y2k.joyreactor.services.requests

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.MyLike
import y2k.joyreactor.requests.MockRequest

/**
 * Created by y2k on 5/3/16.
 */
class PostsForTagRequestTest {

    @Test
    fun testLikeDislike() {
        val httpClient = mock(HttpClient::class.java).apply {
            `when`(getDocument(anyString())).then { MockRequest.loadDocument("test1.html") }
        }

        val request = PostsForTagRequest(httpClient)
        val actual = request.requestAsync(Group.makeTag("Джо Кукан")).toBlocking().last()
        verify(httpClient).getDocument("http://joyreactor.cc/tag/%D0%94%D0%B6%D0%BE+%D0%9A%D1%83%D0%BA%D0%B0%D0%BD")

        assertEquals(10, actual.posts.size)
        assertArrayEquals(arrayOf(
            MyLike.Blocked,
            MyLike.Blocked,
            MyLike.Blocked,
            MyLike.Blocked,
            MyLike.Blocked,
            MyLike.Like,
            MyLike.Dislike,
            MyLike.Unknown,
            MyLike.Unknown,
            MyLike.Unknown),
            actual.posts.map { it.myLike }.toTypedArray())
    }

    @Test
    fun testFavorite() {
        val httpClient = mock(HttpClient::class.java).apply {
            `when`(getDocument(anyString())).then { MockRequest.loadDocument("favorite.html") }
        }

        val request = PostsForTagRequest(httpClient)
        val actual = request.requestAsync(Group.makeFeatured(), "6664").toBlocking().last()
        verify(httpClient).getDocument("http://joyreactor.cc//6664") // FIXME:

        assertEquals(10, actual.posts.size)
        assertArrayEquals(arrayOf(
            true, true, false, false, true,
            false, false, true, true, false),
            actual.posts.map { it.isFavorite }.toTypedArray())
    }
}