package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.Notifications
import y2k.joyreactor.common.await
import y2k.joyreactor.common.awaitPeriodic
import y2k.joyreactor.common.binding
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Post
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.platform.open
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.TagService
import y2k.joyreactor.services.UserService
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by y2k on 3/8/16.
 */
class PostListViewModel(
    private val navigationService: NavigationService,
    private val service: TagService,
    private val userService: UserService,
    private val lifeCycleService: LifeCycleService) {

    val isBusy = binding(false)
    val posts = binding(emptyList<Post?>())
    val hasNewPosts = binding(false)
    val tagMode = binding(0)

    private var group by Delegates.observable(Group.Undefined) { p, old, new ->
        if (old == new) return@observable

        service
            .query(new)
            .awaitPeriodic(lifeCycleService, Notifications.Posts) {
                posts += toViewModelList(it)
                hasNewPosts += it.hasNew
            }

        isBusy += true
        service.preloadNewPosts(new).await { isBusy += false }
    }

    init {
        lifeCycleService.add(BroadcastService.TagSelected::class) { group = it.group }
        group = Group.makeFeatured()

        tagMode
            .asObservable()
            .flatMap { userService.makeGroup(group, qualityFromIndex(it)) }
            .await { group = it }
    }

    private fun qualityFromIndex(it: Int): Group.Quality {
        return when (it) {
            1 -> Group.Quality.Best
            2 -> Group.Quality.All
            else -> Group.Quality.Good
        }
    }

    private fun toViewModelList(it: TagService.State): ArrayList<Post?> {
        val result = ArrayList<Post?>(it.posts)
        it.divider?.let { result.add(it, null) }
        return result
    }

    // ==============================================================
    // Commands
    // ==============================================================

    fun applyNew() {
        hasNewPosts += false
        service.applyNew(group)
    }

    fun loadMore() {
        isBusy += true
        service.loadNextPage(group).await { isBusy += false }
    }

    fun reloadFirstPage() {
        isBusy += true
        service.reloadFirstPage(group).await {
            isBusy += false
            hasNewPosts += false
        }
    }

    // ==============================================================
    // Item commands
    // ==============================================================

    fun itemSelected(position: Int) {
        val post = posts.value[position]
        if (post == null) loadMore() else postClicked(post.id)
    }

    fun postClicked(id: Long) {
        val post = posts.value.firstOrNull { it?.id == id } ?: return
        navigationService.openPost(post.id)
    }

    fun postClicked(position: Int) {
        navigationService.openPost(posts.value[position]!!.id)
    }

    fun playClicked(id: Long) {
        val post = posts.value.firstOrNull { it?.id == id } ?: return
        if (post.image?.isAnimated ?: false) navigationService.openVideo(post.id)
        else navigationService.openImageView(post.id)
    }

    fun playClicked(position: Int) {
        val post = posts.value[position]!!
        if (post.image?.isAnimated ?: false) navigationService.openVideo(post.id)
        else navigationService.openImageView(post.id)
    }

    fun changeLike(position: Int) {
        val post = posts.value[position] ?: return
        navigationService.open<PostLikeViewModel>("" + post.id)
    }
}