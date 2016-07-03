package y2k.joyreactor.services.requests

import org.junit.Assert
import org.junit.Test
import y2k.joyreactor.common.getResult
import y2k.joyreactor.requests.MockHttpClient

/**
 * Created by y2k on 03/07/16.
 */
class ProfileRequestTests {

    @Test
    fun test() {
        val request = ProfileRequest(MockHttpClient())
        val actual = request("golod").getResult()

        Assert.assertEquals(4056.3f, actual.rating)
    }
}