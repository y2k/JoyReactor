package y2k.joyreactor.services

import rx.Completable
import rx.Observable
import rx.subjects.PublishSubject
import y2k.joyreactor.common.peek
import y2k.joyreactor.model.Post
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.PostsForTagRequest
import y2k.joyreactor.services.synchronizers.PostMerger

/**
 * Created by y2k on 11/24/15.
 */
class TagService(private val dataContext: DataContext.Factory,
                 private val postsRequest: PostsForTagRequest,
                 private val merger: PostMerger) {

    private lateinit var group: Group
    private @Volatile lateinit var lastPage: PostsForTagRequest.Data

    private var currentSubscription = PublishSubject.create<Pair<List<Post>, Int?>>()

    fun setTag(group: Group) {
        this.group = group
    }

    fun preloadNewPosts(): Observable<Boolean> {
        return requestAsync().flatMap { merger.isUnsafeUpdate(group, it.posts) }
    }

    fun queryAsync(group: Group): Observable<Pair<List<Post>, Int?>> {
        return currentSubscription!!
    }

    fun applyNew(): Completable {
        return merger
            .mergeFirstPage(group, lastPage.posts)
            .doOnNext { notifyDataChanged() }
            .toCompletable()
    }

    fun loadNextPage(): Completable {
        return requestAsync(lastPage.nextPage)
            .flatMap { merger.mergeNextPage(group, it.posts) }
            .doOnNext { notifyDataChanged() }
            .toCompletable()
    }

    fun reloadFirstPage(): Completable {
        return requestAsync()
            .flatMap { data ->
                dataContext
                    .use { entities ->
                        entities.TagPosts
                            .filter { it.tagId == group.id }
                            .forEach { entities.TagPosts.remove(it) }
                        entities.saveChanges()
                    }
                    .map { data }
            }
            .flatMap { merger.mergeFirstPage(group, it.posts) }
            .doOnNext { notifyDataChanged() }
            .toCompletable()
    }

    private fun notifyDataChanged() {
        dataContext
            .applyUse {
                TagPosts
                    .filter { it.tagId == group.id }
                    .map { link -> Posts.first { it.id == link.postId } }
            }
            .map { it to merger.divider }
            .subscribe { currentSubscription?.onNext(it) }
    }

    fun requestAsync(page: String? = null): Observable<PostsForTagRequest.Data> {
        return postsRequest
            .requestAsync(group, page)
            .peek { lastPage = it }
    }
}