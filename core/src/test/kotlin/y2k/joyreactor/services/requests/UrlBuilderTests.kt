package y2k.joyreactor.services.requests

import org.junit.Assert.assertEquals
import org.junit.Test
import y2k.joyreactor.model.Group

/**
 * Created by y2k on 5/29/16.
 */
class UrlBuilderTests {

    val authority = "http://joyreactor.cc"

    @Test
    fun testFavorite() {
        val urlBuilder = UrlBuilder()

        assertEquals("$authority/user/y2k/favorite",
            urlBuilder.build(Group.makeFavorite("y2k"), null))
        assertEquals("$authority/user/y2k/favorite/123",
            urlBuilder.build(Group.makeFavorite("y2k"), "123"))
    }

    @Test
    fun testTags() {
        val urlBuilder = UrlBuilder()

        assertEquals("$authority/tag/xxx", urlBuilder.build(Group.makeTag("xxx"), null))
        assertEquals("$authority/tag/xxx/123", urlBuilder.build(Group.makeTag("xxx"), "123"))

        assertEquals("$authority/tag/xxx",
            urlBuilder.build(Group(Group.makeTag("xxx"), Group.Quality.Good), null))
        assertEquals("$authority/tag/xxx/123",
            urlBuilder.build(Group(Group.makeTag("xxx"), Group.Quality.Good), "123"))

        assertEquals("$authority/tag/xxx/best",
            urlBuilder.build(Group(Group.makeTag("xxx"), Group.Quality.Best), null))
        assertEquals("$authority/tag/xxx/best/123",
            urlBuilder.build(Group(Group.makeTag("xxx"), Group.Quality.Best), "123"))

        assertEquals("$authority/tag/xxx/all",
            urlBuilder.build(Group(Group.makeTag("xxx"), Group.Quality.All), null))
        assertEquals("$authority/tag/xxx/all/123",
            urlBuilder.build(Group(Group.makeTag("xxx"), Group.Quality.All), "123"))
    }

    @Test
    fun testFeatured() {
        val urlBuilder = UrlBuilder()

        assertEquals("$authority/", urlBuilder.build(Group.makeFeatured(), null))
        assertEquals("$authority/123", urlBuilder.build(Group.makeFeatured(), "123"))

        assertEquals("$authority/",
            urlBuilder.build(Group(Group.makeFeatured(), Group.Quality.Good), null))
        assertEquals("$authority/123",
            urlBuilder.build(Group(Group.makeFeatured(), Group.Quality.Good), "123"))

        assertEquals("$authority/best",
            urlBuilder.build(Group(Group.makeFeatured(), Group.Quality.Best), null))
        assertEquals("$authority/best/123",
            urlBuilder.build(Group(Group.makeFeatured(), Group.Quality.Best), "123"))

        assertEquals("$authority/all",
            urlBuilder.build(Group(Group.makeFeatured(), Group.Quality.All), null))
        assertEquals("$authority/all/123",
            urlBuilder.build(Group(Group.makeFeatured(), Group.Quality.All), "123"))
    }
}