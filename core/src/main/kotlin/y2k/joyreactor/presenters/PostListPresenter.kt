package y2k.joyreactor.presenters

import rx.Observable
import y2k.joyreactor.*
import y2k.joyreactor.common.Messages
import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.services.TagService

/**
 * Created by y2k on 9/26/15.
 */
class PostListPresenter(private val view: PostListPresenter.View, private val serviceFactory: TagService) : Presenter() {

    private var service: TagService? = null

    init {
        getMessages().add({ this.currentTagChanged(it) }, Messages.TagSelected::class.java)
        currentTagChanged(Messages.TagSelected(Tag.makeFeatured()))
    }

    private fun currentTagChanged(m: Messages.TagSelected) {
        service = serviceFactory.make(m.tag)

        view.setBusy(true)
        fromRepository.subscribe { posts -> view.reloadPosts(posts, null) }

        service!!.preloadNewPosts().subscribe(
                { unsafeUpdate ->
                    view.setHasNewPosts(unsafeUpdate!!)
                    view.setBusy(false)
                    if ((!unsafeUpdate)) applyNew()
                }, { it.printStackTrace() })
    }

    fun applyNew() {
        service!!.applyNew().subscribe(
                { posts ->
                    view.setHasNewPosts(false)
                    view.reloadPosts(posts, service!!.divider)
                }, { it.printStackTrace() })
    }

    fun loadMore() {
        view.setBusy(true)
        service!!.loadNextPage().subscribe(
                { posts ->
                    view.reloadPosts(posts, service!!.divider)
                    view.setBusy(false)
                }, { it.printStackTrace() })
    }

    fun reloadFirstPage() {
        view.setBusy(true)
        service!!.reloadFirstPage().subscribe(
                { posts ->
                    view.reloadPosts(posts, posts.size)
                    view.setBusy(false)
                }, { it.printStackTrace() })
    }

    private val fromRepository: Observable<List<Post>>
        get() = service!!.queryAsync()

    fun postClicked(post: Post) {
        Navigation.getInstance().openPost(post.serverId)
    }

    fun playClicked(post: Post) {
        if (post.image.isAnimated)
            Navigation.getInstance().openVideo(post.serverId)
        else
            Navigation.getInstance().openImageView(post)
    }

    interface View {

        fun setBusy(isBusy: Boolean)

        fun reloadPosts(posts: List<Post>, divider: Int?)

        fun setHasNewPosts(hasNewPosts: Boolean)
    }
}