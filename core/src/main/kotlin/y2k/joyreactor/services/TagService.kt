package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import rx.subjects.PublishSubject
import y2k.joyreactor.common.mapDatabase
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.PostsForTagRequest
import y2k.joyreactor.services.synchronizers.PostMerger

/**
 * Created by y2k on 11/24/15.
 */
class TagService(
    private val dataContext: DataContext.Factory,
    private val postsRequest: PostsForTagRequest,
    private val merger: PostMerger,
    private val buffer: MemoryBuffer) {

    private val tagChangedEvent = PublishSubject.create<Unit>()

    fun query(group: Group): Observable<State> {
        return tagChangedEvent
            .mapDatabase(dataContext) {
                TagPosts
                    .filter("groupId" to group.id)
                    .map { Posts.getById(it.postId) }
            }
            .map {
                State(it,
                    buffer.dividers[group.id],
                    buffer.hasNew[group.id] ?: false)
            }
    }

    fun preloadNewPosts(group: Group): Completable {
        notifyDataChanged()
        return requestAsync(group)
            .flatMap { merger.isUnsafeUpdate(group, it.posts) }
            .doOnNext { buffer.hasNew[group.id] = it }
            .flatMap {
                if (it) Observable.empty<Unit>()
                else merger.mergeFirstPage(group, buffer.requests[group.id]!!.posts)
            }
            .doOnCompleted { notifyDataChanged() }
            .toCompletable()
    }

    fun applyNew(group: Group) {
        merger
            .mergeFirstPage(group, buffer.requests[group.id]!!.posts)
            .doOnNext { notifyDataChanged() }
            .subscribe()
    }

    fun loadNextPage(group: Group): Completable {
        return requestAsync(group, buffer.requests[group.id]!!.nextPage)
            .flatMap { merger.mergeNextPage(group, it.posts) }
            .doOnNext { notifyDataChanged() }
            .toCompletable()
    }

    fun reloadFirstPage(group: Group): Completable {
        return requestAsync(group)
            .mapDatabase(dataContext) {
                TagPosts
                    .filter("groupId" to group.id)
                    .forEach { TagPosts.remove(it) }
                saveChanges()
                it
            }
            .flatMap { merger.mergeFirstPage(group, it.posts) }
            .doOnNext { notifyDataChanged() }
            .toCompletable()
    }

    fun notifyDataChanged() {
        tagChangedEvent.onNext(null)
    }

    fun requestAsync(group: Group, page: String? = null): Observable<PostsForTagRequest.Data> {
        return postsRequest
            .requestAsync(group, page)
            .doOnNext { buffer.requests[group.id] = it }
    }

    data class State(val posts: List<Post>, val divider: Int?, val hasNew: Boolean)
}