package y2k.joyreactor.services

import rx.Completable

/**
 * Created by y2k on 04/12/15.
 */
class CommentService(
    private val requestFactory: (Long, String) -> Completable,
    private val postService: PostService) {

    fun createComment(postId: Long, commentText: String): Completable {
        return requestFactory(postId, commentText).andThen(postService.synchronizePostWithImage(postId.toLong()))
    }
}