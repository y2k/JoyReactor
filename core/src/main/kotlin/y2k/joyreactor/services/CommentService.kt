package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.services.requests.CreateCommentRequestFactory
import y2k.joyreactor.services.requests.PostRequest

/**
 * Created by y2k on 04/12/15.
 */
class CommentService(
    private val requestFactory: CreateCommentRequestFactory,
    private val postService: PostService) {

    fun createComment(postId: String, commentText: String): Completable {
        return requestFactory
            .create(postId, commentText)
            .flatMap { postService.synchronizePost(postId.toLong()).toObservable<Any>() }
            .toCompletable()
    }
}