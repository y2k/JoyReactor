package y2k.joyreactor.services

import y2k.joyreactor.common.Notifications
import y2k.joyreactor.common.async.CompletableContinuation
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
    private val dataContext: Entities,
    private val postsRequest: PostsForTagRequest,
    private val merger: PostMerger,
    private val buffer: MemoryBuffer) {

    fun queryPosts(group: Group): Pair<CompletableContinuation<ListState>, Notifications> {
        return dataContext
            .use {
                TagPosts
                    .filter("groupId" to group.id)
                    .map { Posts.getById(it.postId) }
            }
            .then {
                ListState(it,
                    buffer.dividers[group.id],
                    buffer.hasNew[group.id] ?: false)
            } to Notifications.Posts
    }

    fun preloadNewPosts(group: Group): CompletableContinuation<*> {
        notifyDataChanged()
        return requestAsync(group)
            .thenAsync { merger.isUnsafeUpdate(group, it.posts) }
            .then { buffer.hasNew[group.id] = it; it }
            .thenAsync {
                if (it) CompletableContinuation.just(null)
                else merger.mergeFirstPage(group, buffer.requests[group.id]!!.posts)
            }
            .then { notifyDataChanged() }
    }

    fun applyNew(group: Group) {
        merger
            .mergeFirstPage(group, buffer.requests[group.id]!!.posts)
            .then { notifyDataChanged() }
    }

    fun loadNextPage(group: Group): CompletableContinuation<*> {
        return requestAsync(group, buffer.requests[group.id]!!.nextPage)
            .thenAsync { merger.mergeNextPage(group, it.posts) }
            .then { notifyDataChanged() }
    }

    fun reloadFirstPage(group: Group): CompletableContinuation<*> {
        return requestAsync(group)
            .thenAsync(dataContext) {
                TagPosts
                    .filter("groupId" to group.id)
                    .forEach { TagPosts.remove(it) }
                saveChanges()
                it
            }
            .thenAsync { merger.mergeFirstPage(group, it.posts) }
            .then { notifyDataChanged() }
    }

    fun notifyDataChanged() = BroadcastService.broadcast(Notifications.Posts)

    fun requestAsync(group: Group, page: String? = null): CompletableContinuation<PostsForTagRequest.Data> {
        return postsRequest
            .requestAsync(group, page)
            .then {
                buffer.requests[group.id] = it
                it
            }
    }
}