package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.services.requests.CreateCommentRequestFactory
import y2k.joyreactor.services.requests.PostRequest

/**
 * Created by y2k on 04/12/15.
 */
class CommentService(
        private val requestFactory: CreateCommentRequestFactory,
        private val postRequest: PostRequest,
        private val postBuffer: PostDataBuffer) {

    fun createComment(postId: String, commentText: String): Observable<Void> {
        return requestFactory
                .create(postId, commentText)
                .flatMap {
                    ObservableUtils.action {
                        postRequest.request(postId);
                        postBuffer.updatePost(postRequest)
                    }
                }
    }
}