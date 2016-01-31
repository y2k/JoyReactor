package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.common.peek
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.PostsForTagRequest
import y2k.joyreactor.services.synchronizers.PostMerger

/**
 * Created by y2k on 11/24/15.
 */
class TagService(private val dataContext: DataContext.Factory,
                 private val postsRequest: PostsForTagRequest) {

    private lateinit var tag: Tag
    private var merger: PostMerger? = null

    private @Volatile lateinit var lastPage: PostsForTagRequest.Data

    val divider: Int?
        get() = merger?.divider

    fun setTag(tag: Tag) {
        this.tag = tag
        merger = PostMerger(tag, dataContext)
    }

    fun preloadNewPosts(): Observable<Boolean> {
        return requestAsync().flatMap { merger!!.isUnsafeUpdate(it.posts) }
    }

    fun applyNew(): Observable<List<Post>> {
        return merger!!
            .mergeFirstPage(lastPage.posts)
            .flatMap { getFromRepository() }
    }

    fun loadNextPage(): Observable<List<Post>> {
        return requestAsync(lastPage.nextPage)
            .flatMap { merger!!.mergeNextPage(it.posts) }
            .flatMap { getFromRepository() }
    }

    fun reloadFirstPage(): Observable<List<Post>> {
        return requestAsync()
            .flatMap { data ->
                dataContext
                    .use { entities ->
                        entities.TagPosts
                            .filter { it.tagId == tag.id }
                            .forEach { entities.TagPosts.remove(it) }
                        entities.saveChanges()
                    }
                    .map { data }
            }
            .flatMap { merger!!.mergeFirstPage(it.posts) }
            .flatMap { getFromRepository() }
    }

    fun requestAsync(page: String? = null): Observable<PostsForTagRequest.Data> {
        return postsRequest
            .requestAsync(tag, page)
            .peek { lastPage = it }
    }

    fun queryAsync(): Observable<List<Post>> {
        return getFromRepository()
    }

    private fun getFromRepository(): Observable<List<Post>> {
        return dataContext.use { entities ->
            entities.TagPosts
                .filter { it.tagId == tag!!.id }
                .map { link -> entities.Posts.first { it.id == link.postId } }
        }
    }
}