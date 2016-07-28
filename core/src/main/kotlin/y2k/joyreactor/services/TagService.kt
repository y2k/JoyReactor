package y2k.joyreactor.services

import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.async.then
import y2k.joyreactor.model.ListState
import y2k.joyreactor.model.PostsWithNext
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.PostsForTagRequest
import y2k.joyreactor.services.synchronizers.PostMerger

/**
 * Created by y2k on 11/24/15.
 */
class TagService(
    private val entities: Entities,
    private val requestPosts: PostsForTagRequest,
    private val merger: PostMerger,
    private val buffer: MemoryBuffer) {

    fun queryPostsAsync(groupId: String): CompletableFuture<ListState> {
        return entities
            .useAsync {
                TagPosts
                    .filter("groupId" to groupId)
                    .map { Posts.getById(it.postId) }
            }
            .then {
                ListState(it,
                    buffer.dividers[groupId],
                    buffer.hasNew[groupId] ?: false)
            }
    }

    fun preloadNewPosts(groupId: String): CompletableFuture<*> {
        return async_ {
//            val group = await(entities.useAsync { Tags.getById(groupId) })
//            await(entities.useAsync {
//                if (Tags.getByIdOrNull(group.id) == null)
//                    Tags.add(group)
//            })
            val data = await(requestAsync(groupId))
            val unsafe = await(merger.isUnsafeUpdate(groupId, data.posts))
            buffer.hasNew[groupId] = unsafe

            if (!unsafe)
                await(merger.mergeFirstPage(groupId, buffer.requests[groupId]!!.posts))
        }
    }

    fun applyNew(groupId: String): CompletableFuture<*> {
        return merger.mergeFirstPage(groupId, buffer.requests[groupId]!!.posts)
//        return entities
//            .useAsync { Tags.getById(groupId). }
//            .thenAsync { merger.mergeFirstPage(it, buffer.requests[it.id]!!.posts) }
    }

    fun loadNextPage(groupId: String): CompletableFuture<*> {
        return async_ {
            val data = await(requestAsync(groupId, buffer.requests[groupId]!!.nextPage))
            await(merger.mergeNextPage(groupId, data.posts))
        }
    }

    fun requestAsync(groupId: String, page: String? = null): CompletableFuture<PostsWithNext> {
        return requestPosts(groupId, page)
            .then { buffer.requests[groupId] = it; it }
    }

    fun reloadFirstPage(groupId: String): CompletableFuture<*> {
        return async_ {
            val data = await(requestAsync(groupId))

            await(entities.useAsync {
                TagPosts
                    .filter("groupId" to groupId)
                    .forEach { TagPosts.remove(it) }
                saveChanges()
            })
            await(merger.mergeFirstPage(groupId, data.posts))
        }
    }
}