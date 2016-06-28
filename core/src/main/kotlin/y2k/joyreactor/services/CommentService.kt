package y2k.joyreactor.services

import rx.Completable
import y2k.joyreactor.services.requests.CreateCommentRequestFactory

/**
 * Created by y2k on 04/12/15.
 */
class CommentService(
    private val requestFactory: CreateCommentRequestFactory,
    private val postService: PostService) {

    fun createComment(postId: String, commentText: String): Completable {
        return requestFactory
            .create(postId, commentText)
            .flatMap { postService.synchronizePostWithImage(postId.toLong()).toObservable<Any>() }
            .toCompletable()
    }
}