package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ui
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.common.property
import y2k.joyreactor.common.subscribe
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.ListState
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.TagService
import java.util.*

/**
 * Created by y2k on 3/8/16.
 */
class PostListViewModel(
    private val navigationService: NavigationService,
    private val lifeCycleService: LifeCycleService,
    private val service: TagService,
    private val postService: PostService,
    private val group: Group) {

    val isBusy = property(false)
    val posts = property(emptyList<Post?>())
    val hasNewPosts = property(false)
    val isError = property(false)

    init {
        service
            .queryPosts(group)
            .subscribe(lifeCycleService) {
                posts += toViewModelList(it)
                hasNewPosts += it.hasNew
            }

        isBusy += true
        service
            .preloadNewPosts(group)
            .ui({ isBusy += false }, {
                isError += true
                isBusy += false
            })
    }

    private fun toViewModelList(it: ListState): ArrayList<Post?> {
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
        service.loadNextPage(group).ui { isBusy += false }
    }

    fun reloadFirstPage() {
        isBusy += true
        isError += false
        service.reloadFirstPage(group)
            .ui({
                isBusy += false
                hasNewPosts += false
            }, {
                isBusy += false
                isError += true
            })
    }

    // ==============================================================
    // Item commands
    // ==============================================================

    fun postClicked(position: Int) {
        navigationService.open<PostViewModel>(posts.value[position]!!.id)
    }

    fun playClicked(position: Int) {
        val post = posts.value[position] ?: return
        if (post.image?.isAnimated ?: false) navigationService.open<VideoViewModel>(post.id)
        else navigationService.open<ImageViewModel>(post.image!!.fullUrl())
    }

    fun changeLike(position: Int) {
        val post = posts.value[position] ?: return
        navigationService.open<PostLikeViewModel>("" + post.id)
    }

    fun toggleFavorite(position: Int) {
        val post = posts.value[position] ?: return
        postService.toggleFavorite(post.id).ui {}
    }
}