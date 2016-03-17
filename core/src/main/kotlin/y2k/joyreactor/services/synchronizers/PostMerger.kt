package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.common.toArrayList
import y2k.joyreactor.common.unionOrdered
import y2k.joyreactor.model.Post
import y2k.joyreactor.model.Tag
import y2k.joyreactor.model.TagPost
import y2k.joyreactor.services.repository.DataContext
import java.util.*

/**
 * Created by y2k on 10/31/15.
 */
class PostMerger(
    private val dataContext: DataContext.Factory) {

    var divider: Int? = null
        private set

    fun mergeFirstPage(tag: Tag, newPosts: List<Post>): Observable<Unit> {
        return updatePostsAsync(newPosts)
            .flatMap {
                dataContext.use { entities ->
                    divider = newPosts.size

                    val result = ArrayList<TagPost>()
                    for (s in newPosts)
                        result.add(TagPost(tag.id, s.id))
                    entities.TagPosts
                        .filter { it.tagId == tag.id }
                        .filterNot { s -> newPosts.any { it.id == s.postId } }
                        .forEach { result.add(it) }

                    entities.TagPosts
                        .filter { it.tagId == tag.id }
                        .forEach { entities.TagPosts.remove(it) }

                    // TODO: Понять почему здесь падает
                    //                    result.forEach { entities.TagPosts.add(it) }
                    for (s in result) entities.TagPosts.add(s)

                    entities.saveChanges()
                }
            }
    }

    fun isUnsafeUpdate(tag: Tag, newPosts: List<Post>): Observable<Boolean> {
        return dataContext.use { entities ->
            val oldPosts = entities.getPostsForTag(tag)
            if (oldPosts.size == 0) return@use false
            if (newPosts.size > oldPosts.size) return@use true
            for (i in newPosts.indices) {
                val oldId = oldPosts[i].serverId
                val newId = newPosts[i].serverId
                if (oldId != newId) return@use true
            }
            false
        }
    }

    private fun DataContext.getPostsForTag(tag: Tag): List<Post> {
        return TagPosts
            .filter { it.tagId == tag.id }
            .map { tp -> Posts.first { it.id == tp.postId } }
    }

    fun mergeNextPage(tag: Tag, newPosts: List<Post>): Observable<Unit> {
        return updatePostsAsync(newPosts)
            .flatMap {
                dataContext.use { entities ->
                    var links = entities.TagPosts.filter { it.tagId == tag.id }
                    val actualPosts = links.subList(0, divider!!).toArrayList()
                    val expiredPosts = links.subList(divider!!, links.size).toArrayList()

                    for (p in newPosts) {
                        addIfNew(tag, actualPosts, p)
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
                val old = entities.Posts.firstOrNull { it.serverId == p.serverId }
                if (old == null) entities.Posts.add(p)
                else {
                    p.id = old.id
                    entities.Posts.remove(old)
                    entities.Posts.add(p)
                }
            }
            entities.saveChanges()
        }
    }

    private fun addIfNew(tag: Tag, list: MutableList<TagPost>, item: Post) {
        for (s in list)
            if (s.postId == item.id) return
        list.add(TagPost(tag.id, item.id))
    }

    private fun remove(list: MutableList<TagPost>, item: Post) {
        val iterator = list.iterator()
        while (iterator.hasNext())
            if (iterator.next().postId == item.id) iterator.remove()
    }
}