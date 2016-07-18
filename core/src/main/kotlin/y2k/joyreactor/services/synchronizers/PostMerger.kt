package y2k.joyreactor.services.synchronizers

import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.then
import y2k.joyreactor.common.async.thenAsync
import y2k.joyreactor.common.toArrayList
import y2k.joyreactor.common.unionOrdered
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.GroupPost
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.MemoryBuffer
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.repository.Entities
import java.util.*

/**
 * Created by y2k on 10/31/15.
 */
class PostMerger(
    private val buffer: MemoryBuffer,
    private val dataContext: Entities) {

    fun mergeFirstPage(group: Group, newPosts: List<Post>): CompletableFuture<*> {
        return updatePostsAsync(newPosts)
            .thenAsync {
                dataContext.use {
                    buffer.dividers[group.id] = newPosts.size

                    val result = ArrayList<GroupPost>()
                    for (s in newPosts)
                        result.add(GroupPost(group.id, s.id))
                    TagPosts
                        .filter("groupId" to group.id)
                        .filterNot { s -> newPosts.any { it.id == s.postId } }
                        .forEach { result.add(it) }

                    TagPosts
                        .filter("groupId" to group.id)
                        .forEach { TagPosts.remove(it) }

                    // TODO: Понять почему здесь падает
                    //                    result.forEach { entities.TagPosts.add(it) }
                    for (s in result) TagPosts.add(s)

                    saveChanges()
                }
            }
            .then { buffer.hasNew[group.id] = false }
    }

    fun isUnsafeUpdate(group: Group, newPosts: List<Post>): CompletableFuture<Boolean> {
        return dataContext.use {
            val oldPosts = getPostsForTag(group)
            if (oldPosts.size == 0) return@use false
            if (newPosts.size > oldPosts.size) return@use true
            for (i in newPosts.indices) {
                val oldId = oldPosts[i].id
                val newId = newPosts[i].id
                if (oldId != newId) return@use true
            }
            false
        }
    }

    private fun DataContext.getPostsForTag(group: Group): List<Post> {
        return TagPosts
            .filter("groupId" to group.id)
            .map { Posts.getById(it.postId) }
    }

    fun mergeNextPage(group: Group, newPosts: List<Post>): CompletableFuture<*> {
        return updatePostsAsync(newPosts)
            .thenAsync {
                dataContext.use {
                    val links = TagPosts.filter("groupId" to group.id)
                    val actualPosts = links.subList(0, buffer.dividers[group.id]!!).toArrayList()
                    val expiredPosts = links.subList(buffer.dividers[group.id]!!, links.size).toArrayList()

                    for (p in newPosts) {
                        addIfNew(group, actualPosts, p)
                        remove(expiredPosts, p)
                    }
                    buffer.dividers[group.id] = actualPosts.size

                    links.forEach { TagPosts.remove(it) }
                    actualPosts
                        .unionOrdered(expiredPosts)
                        .forEach { TagPosts.add(it) }

                    saveChanges()
                }
            }
    }

    private fun updatePostsAsync(newPosts: List<Post>): CompletableFuture<*> {
        return dataContext.use {
            for (p in newPosts) {
                val old = Posts.getByIdOrNull(p.id)
                if (old == null) Posts.add(p)
                else {
                    Posts.remove(old)
                    Posts.add(p)
                }
            }
            saveChanges()
        }
    }

    private fun addIfNew(group: Group, list: MutableList<GroupPost>, item: Post) {
        for (s in list)
            if (s.postId == item.id) return
        list.add(GroupPost(group.id, item.id))
    }

    private fun remove(list: MutableList<GroupPost>, item: Post) {
        val iterator = list.iterator()
        while (iterator.hasNext())
            if (iterator.next().postId == item.id) iterator.remove()
    }
}