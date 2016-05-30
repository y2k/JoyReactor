package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import rx.Single
import y2k.joyreactor.common.*
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.model.*
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.ChangePostFavoriteRequest
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
    private val broadcastService: BroadcastService,
    private val changePostFavoriteRequest: ChangePostFavoriteRequest) {

    fun toggleFavorite(postId: Long): Completable {
        return dataContext
            .applyUse { Posts.first("id" to postId) }
            .flatMap { changePostFavoriteRequest.execute(postId, !it.isFavorite) }
            .mapDatabase(dataContext) {
                Posts.updateAll("id" to postId) { it.copy(isFavorite = !it.isFavorite) }
                broadcastService.broadcast(Notifications.Posts)
            }
            .toCompletable()
    }

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
        }.flatMap {
            dataContext.applyUse { Posts.getById(postId.toLong()) }
        }.flatMap {
            imageRequestFactory.request(it.image!!.fullUrl(null))
        }.doOnCompleted {
            broadcastService.broadcast(Notifications.Post)
        }.toCompletable()
    }

    fun getPost(postId: Long): Single<Post> {
        return ioObservable { buffer.post }.toSingle()
    }

    fun getTopComments(count: Int, postId: Long): Single<List<Comment>> {
        return RootComments.create(buffer, postId)
            .map { it.filter { it.level == 0 }.sortedByDescending { it.rating }.take(10) }
            .toSingle()
    }

    fun getComments(postId: Long, parentCommentId: Long): Single<CommentGroup> {
        return when (parentCommentId) {
            0L -> RootComments.create(buffer, postId)
            else -> ChildComments.create(buffer, parentCommentId)
        }.toSingle()
    }

    fun getCommentsAsync(postId: Long, parentCommentId: Long): Observable<CommentGroup> {
        return when (parentCommentId) {
            0L -> RootComments.create(buffer, postId)
            else -> ChildComments.create(buffer, parentCommentId)
        }
    }

    fun getFromCache(postId: String): Observable<Post> {
        return dataContext.applyUse { Posts.getById(postId.toLong()) }
    }

    fun getImages(postId: Long): Single<List<Image>> {
        return Single.fromCallable {
            val postAttachments = buffer.attachments.map { it.image }
            val commentAttachments = buffer.comments
                .map { it.attachmentObject }
                .filterNotNull()
            postAttachments.union(commentAttachments).toList()
        }
    }

    fun getPostImages(): Observable<List<Image>> {
        val postAttachments = buffer.attachments.map { it.image }
        val commentAttachments = buffer.comments
            .map { it.attachmentObject }
            .filterNotNull()
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

    fun mainImageFromDisk(serverPostId: Long): Single<PartialResult<File>> {
        return dataContext
            .applyUse { Posts.getById(serverPostId) }
            .flatMap { imageRequestFactory.requestFromCache(it.image!!.fullUrl(null)) }
            .map { PartialResult.complete(it) }
            .toSingle()
    }

    fun mainImage(serverPostId: Long): Observable<File> {
        return dataContext
            .applyUse { Posts.getById(serverPostId) }
            .flatMap { imageRequestFactory.request(it.image!!.fullUrl(null)) }
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