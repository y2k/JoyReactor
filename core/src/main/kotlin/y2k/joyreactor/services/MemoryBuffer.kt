package y2k.joyreactor.services

import y2k.joyreactor.services.requests.PostsForTagRequest
import java.util.*

/**
 * Created by y2k on 12/23/15.
 */
object MemoryBuffer {

    val hasNew = makeMap<Long, Boolean>()
    val requests = makeMap<Long, PostsForTagRequest.Data>()
    val dividers = makeMap<Long, Int>()

    private fun <T, R> makeMap(): MutableMap<T, R> = Collections.synchronizedMap(HashMap<T, R>())
}