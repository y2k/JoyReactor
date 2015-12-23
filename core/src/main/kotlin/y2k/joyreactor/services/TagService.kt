package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.synchronizers.PostListFetcher

/**
 * Created by y2k on 11/24/15.
 */
class TagService(private val dataContext: DataContext.Factory,
                 private val synchronizerFactory: PostListFetcher.Factory) {

    private var synchronizer: PostListFetcher? = null
    private var tag: Tag? = null

    fun setTag(tag: Tag) {
        this.tag = tag
        this.synchronizer = synchronizerFactory.make(tag)
    }

    fun preloadNewPosts(): Observable<Boolean> {
        return synchronizer!!.preloadNewPosts()
    }

    fun applyNew(): Observable<List<Post>> {
        return synchronizer!!
                .applyNew()
                .flatMap({ s -> getFromRepository() })
    }

    val divider: Int?
        get() = synchronizer!!.divider

    fun loadNextPage(): Observable<List<Post>> {
        return synchronizer!!
                .loadNextPage()
                .flatMap({ s -> getFromRepository() })
    }

    fun reloadFirstPage(): Observable<List<Post>> {
        return synchronizer!!
                .reloadFirstPage()
                .flatMap({ s -> getFromRepository() })
    }

    fun queryAsync(): Observable<List<Post>> {
        return getFromRepository()
    }

    private fun getFromRepository(): Observable<List<Post>> {
        return dataContext.using { entities ->
            val tagPosts = entities.TagPosts.filter { it.tagId == tag!!.id }
            entities.Posts.filter { post -> tagPosts.any { it.postId == post.id } }
        }
    }
}