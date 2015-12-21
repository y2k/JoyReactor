package y2k.joyreactor.services.synchronizers

import rx.Observable
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.TagPost
import y2k.joyreactor.services.repository.Repository
import y2k.joyreactor.services.repository.TagPostsForTagQuery
import y2k.joyreactor.services.requests.PostsForTagRequest

/**
 * Created by y2k on 03/11/15.
 */
class PostListFetcher(private val tag: Tag,
                      private val requestFactory: PostsForTagRequest.Factory,
                      private val postRepository: Repository<Post>,
                      private val tagPostRepository: Repository<TagPost>) {

    private val merger = PostMerger(tag, postRepository, tagPostRepository)
    private var request: PostsForTagRequest? = null

    fun preloadNewPosts(): Observable<Boolean> {
        request = getPostsForTagRequest(null)
        return request!!.requestAsync()
                .flatMap { s -> merger.isUnsafeUpdate(request!!.getPosts()) }
    }

    fun applyNew(): Observable<Void> {
        return merger.mergeFirstPage(request!!.getPosts())
    }

    val divider: Int?
        get() = merger.divider

    fun loadNextPage(): Observable<Void> {
        request = getPostsForTagRequest(request!!.nextPageId)
        return request!!.requestAsync()
                .flatMap { s -> merger.mergeNextPage(request!!.getPosts()) }
    }

    fun reloadFirstPage(): Observable<Void> {
        request = getPostsForTagRequest(null)
        return request!!.requestAsync()
                .flatMap { tagPostRepository.deleteWhereAsync(TagPostsForTagQuery(tag)) }
                .flatMap { _void -> merger.mergeFirstPage(request!!.getPosts()) }
    }

    private fun getPostsForTagRequest(pageId: String?): PostsForTagRequest {
        return requestFactory.make(tag, pageId)
    }

    class Factory(
            private val postRepository: Repository<Post>,
            private val tagPostRepository: Repository<TagPost>) {

        private val requestFactory = PostsForTagRequest.Factory()

        fun make(tag: Tag): PostListFetcher {
            return PostListFetcher(tag, requestFactory, postRepository, tagPostRepository)
        }
    }
}