package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.TagPost
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
                    val links = entities.TagPosts.filter { it.tagId == tag.id }
                    val result = ArrayList<TagPost>()

                    for (s in newPosts)
                        result.add(TagPost(tag.id, s.id))
                    for (s in links)
                        if (result.all { it.postId != s.postId })
                            result.add(s)

                    divider = newPosts.size

                    entities.TagPosts
                        .filter { it.tagId == tag.id }
                        .forEach { entities.TagPosts.remove(it) }
                    result.forEach { entities.TagPosts.add(it) }

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
                    val actualPosts = ArrayList(links.subList(0, divider!!))
                    val expiredPosts = ArrayList<TagPost>(links.subList(divider!!, links.size))

                    for (p in newPosts) {
                        addIfNew(tag, actualPosts, p)
                        remove(expiredPosts, p)
                    }
                    divider = actualPosts.size
                    val result = union(actualPosts, expiredPosts)

                    links.forEach { entities.TagPosts.remove(it) }
                    result.forEach { entities.TagPosts.add(it) }

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

    private fun remove(list: ArrayList<TagPost>, item: Post) {
        val iterator = list.iterator()
        while (iterator.hasNext())
            if (iterator.next().postId == item.id) iterator.remove()
    }

    private fun union(left: MutableList<TagPost>, right: List<TagPost>): List<TagPost> {
        left.addAll(right)
        return left
    }
}