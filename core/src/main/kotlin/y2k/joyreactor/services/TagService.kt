package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import rx.subjects.PublishSubject
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.PostsForTagRequest
import y2k.joyreactor.services.synchronizers.PostMerger

/**
 * Created by y2k on 11/24/15.
 */
class TagService(private val dataContext: DataContext.Factory,
                 private val postsRequest: PostsForTagRequest,
                 private val merger: PostMerger,
                 private val buffer: MemoryBuffer) {

    private val tagChangedEvent = PublishSubject.create<Unit>()

    fun query(group: Group): Observable<State> {
        return tagChangedEvent
            .flatMap {
                dataContext
                    .applyUse {
                        TagPosts
                            .filter { it.groupId == group.id }
                            .map { link -> Posts.first { it.id == link.postId } }
                    }
            }
            .map { State(it, buffer.dividers[group.id], buffer.hasNew[group.id]!!) }
    }

    fun preloadNewPosts(group: Group): Completable {
        return requestAsync(group)
            .flatMap { merger.isUnsafeUpdate(group, it.posts) }
            .doOnNext { buffer.hasNew[group.id] = it }
            .flatMap {
                if (it) Observable.empty<Unit>()
                else applyNew(group).toObservable<Unit>()
            }
            .doOnCompleted { notifyDataChanged() }
            .toCompletable()
    }

    fun applyNew(group: Group): Completable {
        return merger
            .mergeFirstPage(group, buffer.requests[group.id]!!.posts)
            .doOnNext { notifyDataChanged() }
            .toCompletable()
    }

    fun loadNextPage(group: Group): Completable {
        return requestAsync(group, buffer.requests[group.id]!!.nextPage)
            .flatMap { merger.mergeNextPage(group, it.posts) }
            .doOnNext { notifyDataChanged() }
            .toCompletable()
    }

    fun reloadFirstPage(group: Group): Completable {
        return requestAsync(group)
            .flatMap { data ->
                dataContext
                    .use { entities ->
                        entities.TagPosts
                            .filter { it.groupId == group.id }
                            .forEach { entities.TagPosts.remove(it) }
                        entities.saveChanges()
                    }
                    .map { data }
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