package y2k.joyreactor.services.requests

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.mockito.Mockito.*
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.MyLike
import y2k.joyreactor.model.Post
import y2k.joyreactor.requests.MockRequest

/**
 * Created by y2k on 5/3/16.
 */
class PostsForTagRequestTest {

    @Test
    fun testTitles() {
        val httpClient = mock(HttpClient::class.java).apply {
            `when`(getDocument(anyString())).then { MockRequest.loadDocument("titles.html") }
        }
        val actual = execute(httpClient, Group.makeFeatured())

        assertArrayEquals(arrayOf(
            "", "", "", "", "",
            "Bubblegum", "сам жри", "", "",
            "спектрограмма саундтрека к новому думу"),
            actual.map { it.title }.toTypedArray())
        assertArrayEquals(arrayOf(
            "", "", "", "", "",
            "", "", "", "", "трек cyberedemon промежуток с 4 по 33 секунду"),
            actual.map { it.description }.toTypedArray())
    }

    @Test
    fun testLikeDislike() {
        val httpClient = mock(HttpClient::class.java).apply {
            `when`(getDocument(anyString())).then { MockRequest.loadDocument("test1.html") }
        }

        val actual = execute(httpClient, Group.makeTag("Джо Кукан"))
        verify(httpClient).getDocument("http://joyreactor.cc/tag/%D0%94%D0%B6%D0%BE+%D0%9A%D1%83%D0%BA%D0%B0%D0%BD")

        assertArrayEquals(arrayOf(
            MyLike.Blocked, MyLike.Blocked, MyLike.Blocked, MyLike.Blocked, MyLike.Blocked,
            MyLike.Like, MyLike.Dislike, MyLike.Unknown, MyLike.Unknown, MyLike.Unknown),
            actual.map { it.myLike }.toTypedArray())
    }

    @Test
    fun testFavorite() {
        val httpClient = mock(HttpClient::class.java).apply {
            `when`(getDocument(anyString())).then { MockRequest.loadDocument("favorite.html") }
        }

        val actual = execute(httpClient, Group.makeFeatured(), "6664")
        verify(httpClient).getDocument("http://joyreactor.cc/6664")

        assertArrayEquals(arrayOf(
            true, true, false, false, true,
            false, false, true, true, false),
            actual.map { it.isFavorite }.toTypedArray())
    }

    private fun execute(httpClient: HttpClient, group: Group, page: String? = null): List<Post> {
        return PostsForTagRequest(httpClient, UrlBuilder())
            .requestAsync(group, page)
            .toBlocking().last()
            .posts
    }
}