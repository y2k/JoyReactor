package y2k.joyreactor.services

import y2k.joyreactor.model.*
import y2k.joyreactor.services.requests.PostRequest
import y2k.joyreactor.services.requests.PostsForTagRequest
import java.util.*

/**
 * Created by y2k on 12/23/15.
 */
object MemoryBuffer {

    val hasNew = Collections.synchronizedMap(HashMap<Long, Boolean>())
    val requests = Collections.synchronizedMap(HashMap<Long, PostsForTagRequest.Data>())
    val dividers = Collections.synchronizedMap(HashMap<Long, Int>())

    private @Volatile var request: PostRequest.Response? = null

    val attachments: List<Attachment> get() = request?.attachments ?: emptyList()
    val similarPosts: List<SimilarPost> get() = request?.similarPosts ?: emptyList()

    fun updatePost(request: PostRequest.Response) {
        this.request = request
    }
}