package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.common.toArrayList
import y2k.joyreactor.common.unionOrdered
import y2k.joyreactor.model.Post
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.GroupPost
import y2k.joyreactor.services.repository.DataContext
import java.util.*

/**
 * Created by y2k on 10/31/15.
 */
class PostMerger(
    private val dataContext: DataContext.Factory) {

    var divider: Int? = null
        private set

    fun mergeFirstPage(group: Group, newPosts: List<Post>): Observable<Unit> {
        return updatePostsAsync(newPosts)
            .flatMap {
                dataContext.use { entities ->
                    divider = newPosts.size

                    val result = ArrayList<GroupPost>()
                    for (s in newPosts)
                        result.add(GroupPost(group.id, s.id))
                    entities.TagPosts
                        .filter { it.groupId == group.id }
                        .filterNot { s -> newPosts.any { it.id == s.postId } }
                        .forEach { result.add(it) }

                    entities.TagPosts
                        .filter { it.groupId == group.id }
                        .forEach { entities.TagPosts.remove(it) }

                    // TODO: Понять почему здесь падает
                    //                    result.forEach { entities.TagPosts.add(it) }
                    for (s in result) entities.TagPosts.add(s)

                    entities.saveChanges()
                }
            }
    }

    fun isUnsafeUpdate(group: Group, newPosts: List<Post>): Observable<Boolean> {
        return dataContext.use { entities ->
            val oldPosts = entities.getPostsForTag(group)
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
            .filter { it.groupId == group.id }
            .map { tp -> Posts.first { it.id == tp.postId } }
    }

    fun mergeNextPage(group: Group, newPosts: List<Post>): Observable<Unit> {
        return updatePostsAsync(newPosts)
            .flatMap {
                dataContext.use { entities ->
                    var links = entities.TagPosts.filter { it.groupId == group.id }
                    val actualPosts = links.subList(0, divider!!).toArrayList()
                    val expiredPosts = links.subList(divider!!, links.size).toArrayList()

                    for (p in newPosts) {
                        addIfNew(group, actualPosts, p)
                        remove(expiredPosts, p)
                    }
                    divider = actualPosts.size

                    links.forEach { entities.TagPosts.remove(it) }
                    actualPosts
                        .unionOrdered(expiredPosts)
                        .forEach { entities.TagPosts.add(it) }

                    entities.saveChanges()
                }
            }
    }

    private fun updatePostsAsync(newPosts: List<Post>): Observable<Unit> {
        return dataContext.use { entities ->
            for (p in newPosts) {
                val old = entities.Posts.firstOrNull { it.id == p.id }
                if (old == null) entities.Posts.add(p)
                else {
                    entities.Posts.remove(old)
                    entities.Posts.add(p)
                }
            }
            entities.saveChanges()
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