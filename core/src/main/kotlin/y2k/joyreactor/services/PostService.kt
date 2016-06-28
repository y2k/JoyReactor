package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import rx.Single
import y2k.joyreactor.common.*
import y2k.joyreactor.common.platform.Platform
import y2k.joyreactor.model.*
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.ChangePostFavoriteRequest
import y2k.joyreactor.services.requests.LikePostRequest
import y2k.joyreactor.services.requests.OriginalImageRequestFactory
import y2k.joyreactor.services.requests.PostRequest
import java.io.File

/**
 * Created by y2k on 11/24/15.
 */
class PostService(
    private val requestImage: OriginalImageRequestFactory,
    private val requestPost: (Long) -> Observable<PostRequest.Response>,
    private val entities: Entities,
    private val likePostRequest: LikePostRequest,
    private val platform: Platform,
    private val broadcastService: BroadcastService,
    private val changePostFavoriteRequest: ChangePostFavoriteRequest) {

    fun toggleFavorite(postId: Long): Completable {
        return entities
            .use { Posts.getById(postId) }
            .flatMap { changePostFavoriteRequest(postId, !it.isFavorite) }
            .mapDatabase(entities) {
                Posts.updateAll("id" eq postId, { it.copy(isFavorite = !it.isFavorite) })
                broadcastService.broadcast(Notifications.Posts)
            }
            .toCompletable()
    }

    fun getVideo(postId: String): Observable<File> {
        return getPost(postId.toLong()).toObservable()
            .map { it.image!!.fullUrl("mp4") }
            .flatMap { requestImage(it) }
    }

    fun synchronizePostWithImage(postId: Long): Completable {
        return syncPost(postId)
            .flatMap { syncPostImage(postId) }
            .toCompletable()
    }

    private fun syncPost(postId: Long): Observable<*> {
        return requestPost(postId)
            .mapDatabase(entities) {
                attachments.remove("postId" eq postId)
                it.attachments.forEach { attachments.add(it) }

                similarPosts.remove("parentPostId" eq postId)
                it.similarPosts.forEach { similarPosts.add(it) }

                comments.remove("postId" eq postId)
                it.comments.forEach { comments.add(it) }
            }
            .doOnNext { broadcastService.broadcast(Notifications.Post) }
    }

    private fun syncPostImage(postId: Long): Observable<*> {
        return entities
            .use { Posts.first("id" eq postId) }
            .flatMap { it.image?.let { requestImage(it.original) } }
            .doOnError { broadcastService.broadcast(Notifications.Post) }
            .doOnCompleted { broadcastService.broadcast(Notifications.Post) }
    }

    fun getTopComments(count: Int, postId: Long): Single<List<Comment>> {
        return RootComments.create(entities, postId)
            .map { it.filter { it.level == 0 }.sortedByDescending { it.rating }.take(count) }
    }

    fun getComments(postId: Long, parentCommentId: Long): Single<CommentGroup> {
        return when (parentCommentId) {
            0L -> RootComments.create(entities, postId)
            else -> ChildComments.create(entities, parentCommentId, postId)
        }
    }

    fun getCommentsAsync(postId: Long, parentCommentId: Long): Single<CommentGroup> {
        return when (parentCommentId) {
            0L -> RootComments.create(entities, postId)
            else -> ChildComments.create(entities, parentCommentId, postId)
        }
    }

    fun getCommentsForId(parentCommentId: Long): Single<CommentGroup> {
        return entities
            .useOnce { comments.getById(parentCommentId).postId }
            .flatMap { ChildComments.create(entities, parentCommentId, it) }
    }

    fun getImages(postId: Long): Single<List<Image>> {
        return entities
            .use {
                val postAttachments = attachments
                    .filter("postId" eq postId)
                    .mapNotNull { it.image }
                val commentAttachments = comments
                    .filter("postId" eq postId)
                    .map { it.attachmentObject }
                    .filterNotNull()
                postAttachments.union(commentAttachments).toList()
            }
            .toSingle()
    }

    fun getPostImages(postId: Long): Single<List<Image>> {
        return entities
            .use {
                val postAttachments = attachments
                    .filter("postId" eq postId)
                    .mapNotNull { it.image }
                val commentAttachments = comments
                    .filter("postId" eq postId)
                    .map { it.attachmentObject }
                    .filterNotNull()
                postAttachments.union(commentAttachments).toList()
            }
            .toSingle()
    }

    fun saveImageToGallery(postId: Long): Completable {
        return getPost(postId)
            .toObservable()
            .flatMap { mainImage(it.id) }
            .flatMap { platform.saveToGallery(it) }
            .toCompletable()
    }

    fun mainImageFromDisk(serverPostId: Long): Single<PartialResult<File>> {
        return entities
            .use { Posts.getById(serverPostId) }
            .flatMap { requestImage.requestFromCache(it.image!!.fullUrl(null)) }
            .map { PartialResult.complete(it) }
            .toSingle()
    }

    fun getPost(postId: Long) = entities.use { Posts.getById(postId) }.toSingle()

    fun mainImage(serverPostId: Long): Observable<File> {
        return entities
            .use { Posts.getById(serverPostId) }
            .flatMap { requestImage(it.image!!.fullUrl(null)) }
    }

    fun updatePostLike(postId: Long, like: Boolean): Completable {
        return likePostRequest(postId, like)
            .flatMap {
                entities.use {
                    val post = Posts.getById(postId)
                    Posts.add(post.copy(rating = it.first, myLike = it.second))
                }
            }
            .doOnCompleted { BroadcastService.broadcast(Notifications.Posts) }
            .toCompletable()
    }
}