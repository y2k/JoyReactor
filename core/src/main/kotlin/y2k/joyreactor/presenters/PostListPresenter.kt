package y2k.joyreactor.presenters

import rx.Observable
import y2k.joyreactor.Post
import y2k.joyreactor.Tag
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.TagService

/**
 * Created by y2k on 9/26/15.
 */
class PostListPresenter(
    private val view: PostListPresenter.View,
    private val service: TagService,
    private val lifeCycleService: LifeCycleService) {

    init {
        lifeCycleService.add(BroadcastService.TagSelected::class) { currentTagChanged(it.tag) }
        currentTagChanged(Tag.makeFeatured())
    }

    private fun currentTagChanged(newTag: Tag) {
        service.setTag(newTag)

        view.setBusy(true)
        getFromRepository().subscribeOnMain { view.reloadPosts(it, null) }

        service
            .preloadNewPosts()
            .subscribeOnMain { unsafeUpdate ->
                view.setHasNewPosts(unsafeUpdate)
                view.setBusy(false)
                if ((!unsafeUpdate)) applyNew()
            }
    }

    fun applyNew() {
        service.
            applyNew()
            .subscribeOnMain { posts ->
                view.setHasNewPosts(false)
                view.reloadPosts(posts, service.divider)
            }
    }

    fun loadMore() {
        view.setBusy(true)
        service
            .loadNextPage()
            .subscribeOnMain { posts ->
                view.reloadPosts(posts, service.divider)
                view.setBusy(false)
            }
    }

    fun reloadFirstPage() {
        view.setBusy(true)
        service
            .reloadFirstPage()
            .subscribeOnMain { posts ->
                view.reloadPosts(posts, posts.size)
                view.setBusy(false)
            }
    }

    private fun getFromRepository(): Observable<List<Post>> {
        return service.queryAsync()
    }

    fun postClicked(post: Post) {
        Navigation.instance.openPost(post.serverId!!)
    }

    fun playClicked(post: Post) {
        if (post.image!!.isAnimated) Navigation.instance.openVideo(post.serverId!!)
        else Navigation.instance.openImageView(post)
    }

    interface View {

        fun setBusy(isBusy: Boolean)

        fun reloadPosts(posts: List<Post>, divider: Int?)

        fun setHasNewPosts(hasNewPosts: Boolean)
    }
}