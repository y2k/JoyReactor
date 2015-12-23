package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.Tag
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.PostsForTagRequest

/**
 * Created by y2k on 03/11/15.
 */
class PostListFetcher(private val tag: Tag,
                      private val requestFactory: PostsForTagRequest.Factory,
                      private val dataContext: DataContext.Factory) {

    private val merger = PostMerger(tag, dataContext)
    private var request: PostsForTagRequest? = null

    fun preloadNewPosts(): Observable<Boolean> {
        request = getPostsForTagRequest(null)
        return request!!.requestAsync()
                .flatMap { s -> merger.isUnsafeUpdate(request!!.getPosts()) }
    }

    fun applyNew(): Observable<Unit> {
        return merger.mergeFirstPage(request!!.getPosts())
    }

    val divider: Int?
        get() = merger.divider

    fun loadNextPage(): Observable<Unit> {
        request = getPostsForTagRequest(request!!.nextPageId)
        return request!!.requestAsync()
                .flatMap { s -> merger.mergeNextPage(request!!.getPosts()) }
    }

    fun reloadFirstPage(): Observable<Unit> {
        request = getPostsForTagRequest(null)
        return request!!.requestAsync()
                .flatMap {
                    dataContext.use { entities ->
                        entities.TagPosts
                                .filter { it.tagId == tag.id }
                                .forEach { entities.TagPosts.remove(it) }
                        entities.saveChanges()
                    }
                }
                .flatMap { merger.mergeFirstPage(request!!.getPosts()) }
    }

    private fun getPostsForTagRequest(pageId: String?): PostsForTagRequest {
        return requestFactory.make(tag, pageId)
    }

    class Factory(private val dataContext: DataContext.Factory) {

        private val requestFactory = PostsForTagRequest.Factory()

        fun make(tag: Tag): PostListFetcher {
            return PostListFetcher(tag, requestFactory, dataContext)
        }
    }
}