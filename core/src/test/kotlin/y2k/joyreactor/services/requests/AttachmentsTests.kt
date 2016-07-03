package y2k.joyreactor.services.requests

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import y2k.joyreactor.common.getResult
import y2k.joyreactor.model.Image
import y2k.joyreactor.requests.MockHttpClient
import y2k.joyreactor.services.requests.parser.PostParser

/**
 * Created by y2k on 03/07/16.
 */
class AttachmentsTests {

    @Test
    fun test() {
        val request = PostRequest(MockHttpClient(), PostParser())
        val actual = request(2692649).getResult()

        assertArrayEquals(
            arrayOf(
                Image("http://img1.joyreactor.cc/pics/post/-3199111.gif"),
                Image("http://img.youtube.com/vi/wgMihkirwgQ/0.jpg")),
            actual.attachments.map { it.image }.toTypedArray())
    }
}