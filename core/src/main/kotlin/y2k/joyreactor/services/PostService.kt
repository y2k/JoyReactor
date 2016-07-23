package y2k.joyreactor.services

import y2k.joyreactor.common.*
import y2k.joyreactor.common.async.CompletableFuture
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
    private val requestImage: (String, Boolean) -> CompletableFuture<File?>,
    private val requestPost: (Long) -> CompletableFuture<PostRequest.Response>,
    private val entities: Entities,
    private val likePostRequest: LikePostRequest,
    private val broadcastService: BroadcastService,
    private val changePostFavoriteRequest: ChangePostFavoriteRequest,
    private val backgroundWorks: BackgroundWorks) {

    fun toggleFavorite(postId: Long): CompletableFuture<*> {
        return entities
            .useAsync { Posts.getById(postId) }
            .thenAsync { changePostFavoriteRequest(postId, !it.isFavorite) }
            .thenAsync(entities) {
                Posts.updateAll("id" eq postId, { it.copy(isFavorite = !it.isFavorite) })
            }
    }

    fun syncPostInBackground(postId: Long): String {
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

    private fun syncPost(postId: Long): CompletableFuture<*> {
        return requestPost(postId)
            .thenAsync(entities) {
                attachments.replaceAll("postId" eq postId, it.attachments)
                similarPosts.replaceAll("parentPostId" eq postId, it.similarPosts)
                comments.replaceAll("postId" eq postId, it.comments)
            }
    }

    private fun syncPostImage(postId: Long): CompletableFuture<*> {
        return entities
            .useAsync { Posts.first("id" eq postId) }
            .thenAsync { requestImage(it.image!!.original, false) }
    }

    fun getTopComments(postId: Long, count: Int): CompletableFuture<List<Comment>> {
        return RootComments.create(entities, postId)
            .then { it.filter { it.level == 0 }.sortedByDescending { it.rating }.take(count) }
    }

    fun getCommentsAsync(postId: Long, parentCommentId: Long): CompletableFuture<CommentGroup> {
        return when (parentCommentId) {
            0L -> RootComments.create(entities, postId)
            else -> ChildComments.create(entities, parentCommentId, postId)
        }
    }

    fun getCommentsForId(parentCommentId: Long): CompletableFuture<CommentGroup> {
        return entities
            .useOnce { comments.getById(parentCommentId).postId }
            .thenAsync { ChildComments.create(entities, parentCommentId, it) }
    }

    fun getImages(postId: Long): CompletableFuture<List<Image>> {
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

    fun getPost(postId: Long): CompletableFuture<Post> = entities.useAsync { Posts.getById(postId) }

    fun updatePostLike(postId: Long, like: Boolean): CompletableFuture<*> {
        return likePostRequest(postId, like)
            .thenAsync(entities) {
                val post = Posts.getById(postId)
                Posts.add(post.copy(rating = it.first, myLike = it.second))
            }
    }

    fun getSyncStatus(postId: Long): WorkStatus = backgroundWorks.getStatus(postId.toKey())

    private fun Long.toKey() = "sync-post-" + this
}