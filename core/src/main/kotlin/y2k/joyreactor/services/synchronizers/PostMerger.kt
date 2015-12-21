package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.TagPost
import y2k.joyreactor.common.ObjectUtils
import y2k.joyreactor.common.ObservableUtils
import y2k.joyreactor.services.repository.PostByIdQuery
import y2k.joyreactor.services.repository.PostsForTagQuery
import y2k.joyreactor.services.repository.Repository
import y2k.joyreactor.services.repository.TagPostsForTagQuery

import java.util.ArrayList

/**
 * Created by y2k on 10/31/15.
 */
internal class PostMerger(
        private val tag: Tag,
        private val postRepository: Repository<Post>,
        private val tagPostRepository: Repository<TagPost>) {

    var divider: Int? = null
        private set

    fun mergeFirstPage(newPosts: List<Post>): Observable<Void> {
        return updatePostsAsync(newPosts)
                .flatMap({
                    tagPostRepository
                            .queryAsync(TagPostsForTagQuery(tag))
                            .map({ links ->
                                val result = ArrayList<TagPost>()

                                for (s in newPosts)
                                    result.add(TagPost(tag.id, s.id))
                                for (s in links)
                                    if (!contains(result, s))
                                        result.add(s)

                                divider = newPosts.size
                                result
                            })
                            .flatMap { s ->
                                tagPostRepository.replaceAllAsync(TagPostsForTagQuery(tag), s)
                            }
                })
    }

    private fun contains(list: List<TagPost>, tagPost: TagPost): Boolean {
        for (s in list)
            if (s.postId == tagPost.postId) return true
        return false
    }

    fun isUnsafeUpdate(newPosts: List<Post>): Observable<Boolean> {
        return postRepository
                .queryAsync(PostsForTagQuery(tag))
                .map({ oldPosts ->
                    if (oldPosts.size == 0) false
                    if (newPosts.size > oldPosts.size) true
                    for (i in newPosts.indices) {
                        val oldId = oldPosts.get(i).serverId
                        val newId = newPosts[i].serverId
                        if (!ObjectUtils.equals(oldId, newId)) true
                    }
                    false
                })
    }

    fun mergeNextPage(newPosts: List<Post>): Observable<Void> {
        return updatePostsAsync(newPosts).flatMap({
            tagPostRepository
                    .queryAsync(TagPostsForTagQuery(tag))
                    .map({ links ->
                        val actualPosts = links.subList(0, divider!!)
                        val expiredPosts = ArrayList<TagPost>(links.subList(divider!!, links.size))

                        for (p in newPosts) {
                            addIfNew(actualPosts, p)
                            remove(expiredPosts, p)
                        }
                        divider = actualPosts.size
                        union(actualPosts, expiredPosts)
                    })
                    .flatMap {
                        tagPostRepository.replaceAllAsync(TagPostsForTagQuery(tag), it)
                    }
        })
    }

    private fun updatePostsAsync(newPosts: List<Post>): Observable<Void> {
        return ObservableUtils.create {
            for (p in newPosts) {
                postRepository.insertOrUpdate(PostByIdQuery(p.serverId), p)
            }
        }
    }

    private fun addIfNew(list: MutableList<TagPost>, item: Post) {
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