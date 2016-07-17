package y2k.joyreactor.services

import y2k.joyreactor.common.BackgroundWorks
import y2k.joyreactor.common.WorkStatus
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
    private val entities: Entities,
    private val postsRequest: PostsForTagRequest,
    private val merger: PostMerger,
    private val buffer: MemoryBuffer,
    private val backgroundWorks: BackgroundWorks) {

    fun queryPosts(group: Group): CompletableContinuation<ListState> {
        return entities
            .useAsync {
                TagPosts
                    .filter("groupId" to group.id)
                    .map { Posts.getById(it.postId) }
            }
            .then {
                ListState(it,
                    buffer.dividers[group.id],
                    buffer.hasNew[group.id] ?: false)
            }
    }

    fun requestAsync(group: Group, page: String? = null): CompletableContinuation<PostsForTagRequest.Data> {
        return postsRequest
            .requestAsync(group, page)
            .then {
                buffer.requests[group.id] = it
                it
            }
    }

    fun getSyncStatus(group: Group): WorkStatus = backgroundWorks.getStatus(group.toKey())

    fun preloadNewPosts(group: Group): Any {
        backgroundWorks.markWorkStarted(group.toKey())
        requestAsync(group)
            .thenAsync { merger.isUnsafeUpdate(group, it.posts) }
            .then { buffer.hasNew[group.id] = it; it }
            .thenAsync {
                if (it) CompletableContinuation.just(null)
                else merger.mergeFirstPage(group, buffer.requests[group.id]!!.posts)
            }
            .whenComplete_ { backgroundWorks.markWorkFinished(group.toKey(), it.error) }
        return group.toKey()
    }

    fun applyNew(group: Group) {
        merger.mergeFirstPage(group, buffer.requests[group.id]!!.posts)
    }

    fun loadNextPage(group: Group) {
        backgroundWorks.markWorkStarted(group.toKey())
        requestAsync(group, buffer.requests[group.id]!!.nextPage)
            .thenAsync { merger.mergeNextPage(group, it.posts) }
            .whenComplete_ { backgroundWorks.markWorkFinished(group.toKey(), it.error) }
    }

    fun reloadFirstPage(group: Group) {
        backgroundWorks.markWorkStarted(group.toKey())
        requestAsync(group)
            .thenAsync(entities) {
                TagPosts
                    .filter("groupId" to group.id)
                    .forEach { TagPosts.remove(it) }
                saveChanges()
                it
            }
            .thenAsync { merger.mergeFirstPage(group, it.posts) }
            .whenComplete_ { backgroundWorks.markWorkFinished(group.toKey(), it.error) }
    }

    private fun Group.toKey(): String = serverId
}