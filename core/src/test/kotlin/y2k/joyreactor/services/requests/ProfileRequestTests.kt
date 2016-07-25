package y2k.joyreactor.services.requests

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import y2k.joyreactor.model.Profile
import y2k.joyreactor.requests.MockHttpClient

/**
 * Created by y2k on 03/07/16.
 */
class ProfileRequestTests {

    @Test
    fun golodTest() {
        val request = ProfileRequest(MockHttpClient(), { TODO() })
        val actual = request("golod").get()

        assertEquals(4056.3f, actual.rating)
        assertArrayEquals(
            arrayOf(
                Profile.SubRating(1866.7f, "гиф анимация"),
                Profile.SubRating(1137.0f, "Anime"),
                Profile.SubRating(140.8f, "секретные разделы"),
                Profile.SubRating(99.8f, "Anime Adult"),
                Profile.SubRating(72.1f, "гуро"),
                Profile.SubRating(47.9f, "порно"),
                Profile.SubRating(26.2f, "Бесконечное лето"),
                Profile.SubRating(25.8f, "Naruto"),
                Profile.SubRating(18.4f, "Этти"),
                Profile.SubRating(17.3f, "AMV")
            ),
            actual.subRatings.toTypedArray())

        assertEquals(59, actual.awards.size)
        assertEquals(
            Profile.Award("http://img1.joyreactor.cc/pics/award/1", "За топ I степени"),
            actual.awards.first())
        assertEquals(
            Profile.Award("http://img0.joyreactor.cc/pics/award/928", "\"Гитара Юи Хирасава\" - за попадание в лучшее тега K-On!"),
            actual.awards.last())
    }

    @Test
    fun ikaryTest() {
        val request = ProfileRequest(MockHttpClient(), { TODO() })
        val actual = request("ikari").get()

        assertEquals(3368.7f, actual.rating)
    }
}