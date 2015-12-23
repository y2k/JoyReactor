package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.CommentGroup
import y2k.joyreactor.Image
import y2k.joyreactor.Post
import y2k.joyreactor.SimilarPost
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.services.requests.OriginalImageRequestFactory
import y2k.joyreactor.services.requests.PostRequest
import java.io.File
import java.util.*

/**
 * Created by y2k on 11/24/15.
 */
class PostService(private val imageRequestFactory: OriginalImageRequestFactory,
                  private val postRequest: PostRequest,
                  private val buffer: PostDataBuffer) {

    fun synchronizePostAsync(postId: String): Observable<Post> {
        return ObservableUtils.func {
            postRequest.request(postId);
            buffer.updatePost(postRequest)
            buffer.post
        }
    }

    fun getCommentsAsync(postId: Int, parentCommentId: Int): Observable<CommentGroup> {
        if (parentCommentId == 0)
            return getCommentForPost(postId)

        val parent = buffer.comments.first { it.id == parentCommentId }
        val children = buffer.comments
                .filter { it.parentId == parentCommentId }
                .toList()
        return Observable.just(CommentGroup.OneLevel(parent, children))
    }

    private fun getCommentForPost(postId: Int): Observable<CommentGroup> {
        val firstLevelComments = HashSet<Int>()
        val items = buffer.comments
                .filter { s -> s.postId == postId }
                .filter { s ->
                    if (s.parentId == 0) {
                        firstLevelComments.add(s.id)
                        true
                    } else {
                        firstLevelComments.contains(s.parentId)
                    }
                }
                .toList()
        return Observable.just(CommentGroup.TwoLevel(items))
    }

    fun getFromCache(postId: String): Observable<Post> {
        return Observable.just(buffer.post)
    }

    fun getPostImages(postId: Int): Observable<List<Image>> {
        val postAttachments = buffer.attachments.map { it.image }
        val commentAttachments = buffer.comments
                .filter { it.attachment != null }
                .map { it.attachment }
        return Observable.just(postAttachments.union(commentAttachments).toList())
    }

    fun getSimilarPosts(postId: Int): Observable<List<SimilarPost>> {
        return Observable.just(buffer.similarPosts)
    }

    fun mainImage(serverPostId: String): Observable<File> {
        return Observable
                .just(buffer.post.image!!.fullUrl(null))
                .flatMap({ url -> imageRequestFactory.request(url) })
    }

    fun mainImagePartial(serverPostId: String): Observable<PartialResult<File>> {
        return Observable
                .just(buffer.post.image!!.fullUrl(null))
                .flatMap({ url -> imageRequestFactory.requestPartial(url) })
    }
}