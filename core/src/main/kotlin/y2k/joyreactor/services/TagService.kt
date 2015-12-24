package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.PostsForTagRequest
import y2k.joyreactor.services.synchronizers.PostMerger

/**
 * Created by y2k on 11/24/15.
 */
class TagService(private val dataContext: DataContext.Factory,
                 private val requestFactory: PostsForTagRequest.Factory) {

    private var tag: Tag? = null
    private var merger: PostMerger? = null
    private var request: PostsForTagRequest? = null

    fun setTag(tag: Tag) {
        this.tag = tag
        merger = PostMerger(tag, dataContext)
    }

    fun preloadNewPosts(): Observable<Boolean> {
        request = getPostsForTagRequest()
        return request!!
                .requestAsync()
                .flatMap { merger!!.isUnsafeUpdate(request!!.getPosts()) }
    }

    private fun getPostsForTagRequest(pageId: String? = null): PostsForTagRequest {
        return requestFactory.make(tag!!, pageId)
    }

    fun applyNew(): Observable<List<Post>> {
        return merger!!
                .mergeFirstPage(request!!.getPosts())
                .flatMap({ s -> getFromRepository() })
    }

    val divider: Int?
        get() = merger?.divider

    fun loadNextPage(): Observable<List<Post>> {
        request = getPostsForTagRequest(request!!.nextPageId)
        return request!!.requestAsync()
                .flatMap { s -> merger!!.mergeNextPage(request!!.getPosts()) }
                .flatMap({ s -> getFromRepository() })
    }

    fun reloadFirstPage(): Observable<List<Post>> {
        request = getPostsForTagRequest(null)
        return request!!.requestAsync()
                .flatMap {
                    dataContext.use { entities ->
                        entities.TagPosts
                                .filter { it.tagId == tag!!.id }
                                .forEach { entities.TagPosts.remove(it) }
                        entities.saveChanges()
                    }
                }
                .flatMap { merger!!.mergeFirstPage(request!!.getPosts()) }
                .flatMap({ s -> getFromRepository() })
    }

    fun queryAsync(): Observable<List<Post>> {
        return getFromRepository()
    }

    private fun getFromRepository(): Observable<List<Post>> {
        return dataContext.use { entities ->
            val tagPosts = entities.TagPosts.filter { it.tagId == tag!!.id }
            entities.Posts.filter { post -> tagPosts.any { it.postId == post.id } }
        }
    }
}