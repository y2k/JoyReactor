package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.CommentGroup
import y2k.joyreactor.Image
import y2k.joyreactor.Post
import y2k.joyreactor.SimilarPost
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.OriginalImageRequestFactory
import y2k.joyreactor.services.requests.PostRequest
import java.io.File
import java.util.*

/**
 * Created by y2k on 11/24/15.
 */
class PostService(private val imageRequestFactory: OriginalImageRequestFactory,
                  private val postRequest: PostRequest,
                  private val buffer: MemoryBuffer,
                  private val dataContext: DataContext.Factory) {

    fun synchronizePostAsync(postId: String): Observable<Post> {
        return ObservableUtils.func {
            postRequest.request(postId);
            buffer.updatePost(postRequest)
            buffer.post
        }
    }

    fun getCommentsAsync(postId: Long, parentCommentId: Long): Observable<CommentGroup> {
        if (parentCommentId == 0L)
            return getCommentForPost(postId)

        val parent = buffer.comments.first { it.id == parentCommentId }
        val children = buffer.comments
                .filter { it.parentId == parentCommentId }
                .toList()
        return Observable.just(CommentGroup.OneLevel(parent, children))
    }

    private fun getCommentForPost(postId: Long): Observable<CommentGroup> {
        val firstLevelComments = HashSet<Long>()
        val items = buffer.comments
                .filter { s -> s.postId == postId }
                .filter { s ->
                    if (s.parentId == 0L) {
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
        return dataContext.applyUse { Posts.first { it.serverId == postId } }
    }

    fun getPostImages(postId: Long): Observable<List<Image>> {
        val postAttachments = buffer.attachments.map { it.image }
        val commentAttachments = buffer.comments
                .filter { it.attachment != null }
                .map { it.attachment }
        return Observable.just(postAttachments.union(commentAttachments).toList())
    }

    fun getSimilarPosts(postId: Long): Observable<List<SimilarPost>> {
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