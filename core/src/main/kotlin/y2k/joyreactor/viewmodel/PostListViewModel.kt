package y2k.joyreactor.viewmodel

import rx.Observable
import y2k.joyreactor.common.binding
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.model.Post
import y2k.joyreactor.model.Tag
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.TagService
import java.util.*

/**
 * Created by y2k on 3/8/16.
 */
class PostListViewModel(
    private val navigationService: NavigationService,
    private val service: TagService,
    private val lifeCycleService: LifeCycleService) {

    val isBusy = binding(false)
    val posts = binding(emptyList<Post?>())
    val hasNewPosts = binding(false)

    init {
        lifeCycleService.add(BroadcastService.TagSelected::class) { currentTagChanged(it.tag) }
        currentTagChanged(Tag.makeFeatured())
    }

    private fun currentTagChanged(newTag: Tag) {
        service.setTag(newTag)

        isBusy.value = true
        getFromRepository().subscribeOnMain { reloadPosts(it, null) }

        service
            .preloadNewPosts()
            .subscribeOnMain { unsafeUpdate ->
                hasNewPosts.value = unsafeUpdate
                isBusy.value = false
                if ((!unsafeUpdate)) applyNew()
            }
    }

    fun applyNew() {
        service.
            applyNew()
            .subscribeOnMain {
                hasNewPosts.value = false
                reloadPosts(it, service.divider)
            }
    }

    fun loadMore() {
        isBusy.value = true
        service
            .loadNextPage()
            .subscribeOnMain {
                reloadPosts(it, service.divider)
                isBusy.value = false
            }
    }

    fun reloadFirstPage() {
        isBusy.value = true
        service
            .reloadFirstPage()
            .subscribeOnMain {
                reloadPosts(it, it.size)
                isBusy.value = false
                hasNewPosts.value = false
            }
    }

    private fun reloadPosts(posts: List<Post>, dividerPosition: Int?) {
        val postsWithDiv = ArrayList<Post?>(posts)
        if (dividerPosition != null) postsWithDiv.add(dividerPosition, null)
        this.posts.value = postsWithDiv
    }

    private fun getFromRepository(): Observable<List<Post>> {
        return service.queryAsync()
    }

    fun postClicked(position: Int) {
        navigationService.openPost(posts.value[position]!!.serverId)
    }

    fun playClicked(position: Int) {
        val post = posts.value[position]!!
        if (post.image?.isAnimated ?: false) navigationService.openVideo(post.serverId)
        else navigationService.openImageView(post.serverId)
    }
}