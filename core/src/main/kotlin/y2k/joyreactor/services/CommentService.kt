package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.services.requests.CreateCommentRequestFactory
import y2k.joyreactor.services.synchronizers.PostFetcher

/**
 * Created by y2k on 04/12/15.
 */
class CommentService(
        private val requestFactory: CreateCommentRequestFactory,
        private val postFetcher: PostFetcher) {

    fun createComment(postId: String, commentText: String): Observable<Void> {
        return requestFactory
                .create(postId, commentText)
                .flatMap { postFetcher.synchronizeWithWeb(postId) }
    }
}