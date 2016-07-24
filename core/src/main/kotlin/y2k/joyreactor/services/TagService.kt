package y2k.joyreactor.services

import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.ListState
import y2k.joyreactor.services.repository.Entities
import y2k.joyreactor.services.requests.PostsForTagRequest
import y2k.joyreactor.services.synchronizers.PostMerger

/**
 * Created by y2k on 11/24/15.
 */
class TagService(
    private val entities: Entities,
    private val postsRequest: PostsForTagRequest,
    private val merger: PostMerger,
    private val buffer: MemoryBuffer) {

    fun queryPostsAsync(groupId: Long): CompletableFuture<ListState> {
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

    fun preloadNewPosts(groupId: Long): CompletableFuture<*> {
        return async_ {
            val group = await(entities.useAsync { Tags.getById(groupId) })
            val data = await(requestAsync(group))
            val unsafe = await(merger.isUnsafeUpdate(group, data.posts))
            buffer.hasNew[groupId] = unsafe

            if (!unsafe)
                await(merger.mergeFirstPage(group, buffer.requests[groupId]!!.posts))
        }
    }

    fun requestAsync(group: Group, page: String? = null): CompletableFuture<PostsForTagRequest.Data> {
        return postsRequest
            .requestAsync(group, page)
            .then {
                buffer.requests[group.id] = it
                it
            }
    }

    fun applyNew(groupId: Long): CompletableFuture<*> {
        return entities
            .useAsync { Tags.getById(groupId) }
            .thenAsync { merger.mergeFirstPage(it, buffer.requests[it.id]!!.posts) }
    }

    fun loadNextPage(groupId: Long): CompletableFuture<*> {
        return async_ {
            val group = await(entities.useAsync { Tags.getById(groupId) })
            val data = await(requestAsync(group, buffer.requests[group.id]!!.nextPage))
            await(merger.mergeNextPage(group, data.posts))
        }
    }

    fun reloadFirstPage(groupId: Long): CompletableFuture<*> {
        return async_ {
            val group = await(entities.useAsync { Tags.getById(groupId) })
            val data = await(requestAsync(group))

            await(entities.useAsync {
                TagPosts
                    .filter("groupId" to group.id)
                    .forEach { TagPosts.remove(it) }
                saveChanges()
            })
            await(merger.mergeFirstPage(group, data.posts))
        }
    }
}