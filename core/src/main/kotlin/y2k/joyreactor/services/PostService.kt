package y2k.joyreactor.services

import y2k.joyreactor.common.*
import y2k.joyreactor.common.async.CompletableContinuation
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.model.*
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.ChangePostFavoriteRequest
import y2k.joyreactor.services.requests.LikePostRequest
import y2k.joyreactor.services.requests.PostRequest
import java.io.File

/**
 * Created by y2k on 11/24/15.
 */
class PostService(
    private val requestImage: (String, Boolean) -> CompletableContinuation<File>,
    private val requestPost: (Long) -> CompletableContinuation<PostRequest.Response>,
    private val entities: Entities,
    private val likePostRequest: LikePostRequest,
    private val broadcastService: BroadcastService,
    private val changePostFavoriteRequest: ChangePostFavoriteRequest,
    private val backgroundWorks: BackgroundWorks) {

    fun toggleFavorite(postId: Long): CompletableContinuation<*> {
        return entities
            .use { Posts.getById(postId) }
            .thenAsync { changePostFavoriteRequest(postId, !it.isFavorite) }
            .thenAsync(entities) {
                Posts.updateAll("id" eq postId, { it.copy(isFavorite = !it.isFavorite) })
            }
            .then { broadcastService.broadcast(Notifications.Post) }
    }

    fun syncPostInBackground(postId: Long): Any {
        async_ {
            backgroundWorks.markWorkStarted(postId.toKey())
            try {
                await(syncPost(postId))
                backgroundWorks.updateWorkStatus(postId.toKey())

                await(syncPostImage(postId))
                backgroundWorks.markWorkFinished(postId.toKey())
            } catch (e: Exception) {
                backgroundWorks.markWorkFinished(postId.toKey(), e)
            }
        }

        return postId.toKey()
    }

    private fun syncPost(postId: Long): CompletableContinuation<*> {
        return requestPost(postId)
            .thenAsync(entities) {
                attachments.replaceAll("postId" eq postId, it.attachments)
                similarPosts.replaceAll("parentPostId" eq postId, it.similarPosts)
                comments.replaceAll("postId" eq postId, it.comments)
            }
            .then { broadcastService.broadcast(Notifications.Post) }
    }

    private fun syncPostImage(postId: Long): CompletableContinuation<*> {
        return entities
            .useAsync { Posts.first("id" eq postId) }
            .thenAsync { requestImage(it.image!!.original, false) }
    }

    fun getTopComments(postId: Long, count: Int): CompletableContinuation<List<Comment>> {
        return RootComments.create(entities, postId)
            .then { it.filter { it.level == 0 }.sortedByDescending { it.rating }.take(count) }
    }

    fun getCommentsAsync(postId: Long, parentCommentId: Long): CompletableContinuation<CommentGroup> {
        return when (parentCommentId) {
            0L -> RootComments.create(entities, postId)
            else -> ChildComments.create(entities, parentCommentId, postId)
        }
    }

    fun getCommentsForId(parentCommentId: Long): CompletableContinuation<CommentGroup> {
        return entities
            .useOnce { comments.getById(parentCommentId).postId }
            .thenAsync { ChildComments.create(entities, parentCommentId, it) }
    }

    fun getImages(postId: Long): CompletableContinuation<List<Image>> {
        return entities.useOnce {
            val postAttachments = attachments
                .filter("postId" eq postId)
                .mapNotNull { it.image }
            val commentAttachments = comments
                .filter("postId" eq postId)
                .mapNotNull { it.attachment }
            postAttachments.union(commentAttachments).toList()
        }
    }

    fun getPost(postId: Long): CompletableContinuation<Post> = entities.useAsync { Posts.getById(postId) }

    fun updatePostLike(postId: Long, like: Boolean): CompletableContinuation<*> {
        return likePostRequest(postId, like)
            .thenAsync(entities) {
                val post = Posts.getById(postId)
                Posts.add(post.copy(rating = it.first, myLike = it.second))
            }
            .then { BroadcastService.broadcast(Notifications.Posts) }
    }

    fun getSyncStatus(postId: Long): WorkStatus = backgroundWorks.getStatus(postId.toKey())

    private fun Long.toKey() = "sync-post-" + this
}