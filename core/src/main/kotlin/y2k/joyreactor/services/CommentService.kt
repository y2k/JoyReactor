package y2k.joyreactor.services

import y2k.joyreactor.common.async.CompletableContinuation

/**
 * Created by y2k on 04/12/15.
 */
class CommentService(
    private val requestComment: (Long, String) -> CompletableContinuation<*>,
    private val postService: PostService) {

    fun createComment(postId: Long, commentText: String): CompletableContinuation<*> {
        TODO()
        return requestComment(postId, commentText)
//            .thenAsync { postService.synchronizePostWithImageOld(postId) }
    }
}