package y2k.joyreactor.services

import y2k.joyreactor.*
import y2k.joyreactor.model.*
import y2k.joyreactor.services.requests.PostRequest

/**
 * Created by y2k on 12/23/15.
 */
object MemoryBuffer {

    private var request: PostRequest? = null

    var messages: List<Message> = emptyList()

    val post: Post get() {
        return request!!.post!!
    }

    val comments: List<Comment> get() {
        return request?.comments ?: emptyList()
    }

    val attachments: List<Attachment> get() {
        return request?.getAttachments() ?: emptyList()
    }

    val similarPosts: List<SimilarPost> get() {
        return request?.getSimilarPosts() ?: emptyList()
    }

    fun updatePost(request: PostRequest) {
        this.request = request
    }
}