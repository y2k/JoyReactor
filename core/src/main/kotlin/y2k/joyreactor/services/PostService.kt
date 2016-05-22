package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import rx.Single
import y2k.joyreactor.common.Notifications
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.model.*
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.LikePostRequest
import y2k.joyreactor.services.requests.OriginalImageRequestFactory
import y2k.joyreactor.services.requests.PostRequest
import java.io.File

/**
 * Created by y2k on 11/24/15.
 */
class PostService(
    private val imageRequestFactory: OriginalImageRequestFactory,
    private val postRequest: PostRequest,
    private val buffer: MemoryBuffer,
    private val dataContext: DataContext.Factory,
    private val likePostRequest: LikePostRequest,
    private val platform: Platform,
    private val broadcastService: BroadcastService) {

    fun getVideo(postId: String): Observable<File> {
        return getFromCache(postId)
            .map { it.image!!.fullUrl("mp4") }
            .flatMap { imageRequestFactory.request(it) }
    }

    fun synchronizePost(postId: Long): Completable {
        return ioObservable {
            postRequest.request(postId.toString())
            buffer.updatePost(postRequest)

            broadcastService.broadcast(Notifications.Post)
        }.toCompletable()
    }

    fun synchronizePostAsync(postId: String): Observable<Post> {
        return ioObservable {
            postRequest.request(postId)
            buffer.updatePost(postRequest)
            buffer.post
        }
    }

    fun getPost(postId: Long): Pair<Single<Post>, Notifications> {
        return ioObservable {
            buffer.post
        }.toSingle() to Notifications.Post
    }

    fun getCommentsAsync(postId: Long, parentCommentId: Long): Observable<CommentGroup> {
        return when (parentCommentId) {
            0L -> RootComments.create(buffer, postId)
            else -> ChildComments.create(buffer, parentCommentId)
        }
    }

    fun getComments(postId: Long, parentCommentId: Long): Pair<Single<CommentGroup>, Notifications> {
        return when (parentCommentId) {
            0L -> RootComments.create(buffer, postId)
            else -> ChildComments.create(buffer, parentCommentId)
        }.toSingle() to Notifications.Post
    }

    fun getFromCache(postId: String): Observable<Post> {
        return dataContext.applyUse { Posts.getById(postId.toLong()) }
    }

    fun getImages(postId: Long): Pair<Single<List<Image>>, Notifications> {
        val postAttachments = buffer.attachments.map { it.image }
        val commentAttachments = buffer.comments
            .filter { it.attachmentObject != null }
            .map { it.attachmentObject!! }
        return Observable
            .just(postAttachments.union(commentAttachments).toList())
            .toSingle() to Notifications.Post
    }

    fun getPostImages(): Observable<List<Image>> {
        val postAttachments = buffer.attachments.map { it.image }
        val commentAttachments = buffer.comments
            .filter { it.attachmentObject != null }
            .map { it.attachmentObject!! }
        return Observable.just(postAttachments.union(commentAttachments).toList())
    }

    fun getSimilarPosts(postId: Long): Observable<List<SimilarPost>> {
        return Observable.just(buffer.similarPosts)
    }

    fun saveImageToGallery(postId: Long): Completable {
        return getFromCache("" + postId)
            .flatMap { mainImage(it.id) }
            .flatMap { platform.saveToGallery(it) }
            .toCompletable()
    }

    fun mainImage(serverPostId: Long): Observable<File> {
        return dataContext
            .applyUse { Posts.getById(serverPostId) }
            .flatMap { imageRequestFactory.request(it.image!!.fullUrl(null)) }
    }

    fun mainImagePartial(serverPostId: Long): Observable<PartialResult<File>> {
        return Observable
            .just(buffer.post.image!!.fullUrl(null))
            .flatMap({ url -> imageRequestFactory.requestPartial(url) })
    }

    fun updatePostLike(postId: Long, like: Boolean): Completable {
        return likePostRequest
            .like(postId, like)
            .flatMap {
                dataContext.applyUse {
                    val post = Posts.getById(postId)
                    Posts.add(post.copy(rating = it.first, myLike = it.second))
                }
            }
            .doOnCompleted { BroadcastService.broadcast(Notifications.Posts) }
            .toCompletable()
    }
}