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
            .doEntities(entities) {
                Posts.updateAll("id" eq postId, { it.copy(isFavorite = !it.isFavorite) })
            }
            .doOnTerminate { broadcastService.broadcast(Notifications.Post) }
    }

    fun getVideo(postId: String): Single<File> {
        return getPost(postId.toLong())
            .map { it.image!!.fullUrl("mp4") }
            .flatMap { requestImage(it) }
    }

    fun synchronizePostWithImage(postId: Long): Completable {
        return syncPost(postId)
            .andThen(syncPostImage(postId))
            .doOnTerminate { broadcastService.broadcast(Notifications.Post) }
    }

    private fun syncPost(postId: Long): Completable {
        return requestPost(postId)
            .doEntities(entities) {
                attachments.replaceAll("postId" eq postId, it.attachments)
                similarPosts.replaceAll("parentPostId" eq postId, it.similarPosts)
                comments.replaceAll("postId" eq postId, it.comments)

                broadcastService.broadcast(Notifications.Post)
            }
    }

    private fun syncPostImage(postId: Long): Completable {
        return entities
            .useOnce { Posts.first("id" eq postId) }
            .flatMap { it.image?.let { requestImage(it.original) } }
            .toCompletable()
    }

    fun getTopComments(postId: Long, count: Int): Single<List<Comment>> {
        return RootComments.create(entities, postId)
            .map { it.filter { it.level == 0 }.sortedByDescending { it.rating }.take(count) }
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
        return entities.useOnce {
            val postAttachments = attachments
                .filter("postId" eq postId)
                .mapNotNull { it.image }
            val commentAttachments = comments
                .filter("postId" eq postId)
                .mapNotNull { it.attachmentObject }
            postAttachments.union(commentAttachments).toList()
        }
    }

    fun saveImageToGallery(postId: Long): Completable {
        return getPost(postId)
            .flatMap { mainImage(it.id) }
            .andThen { platform.saveToGallery(it) }
    }

    fun mainImageFromDisk(serverPostId: Long): Single<File?> {
        return entities
            .useOnce { Posts.getById(serverPostId) }
            .flatMap {
                when {
                    it.image == null -> Single.just(null)
                    it.image.isAnimated -> {
                        requestImage(it.image.original, onlyFromCache = true)
                            .flatMap { platform.createTmpThumbnail(it) }
                    }
                    else -> requestImage(it.image.original, onlyFromCache = true)
                }
            }
    }

    fun getPost(postId: Long): Single<Post> = entities.useOnce { Posts.getById(postId) }

    fun mainImage(serverPostId: Long): Single<File> {
        return entities
            .useOnce { Posts.getById(serverPostId) }
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