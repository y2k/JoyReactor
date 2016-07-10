package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ListWithDivider
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.property
import y2k.joyreactor.common.subscribe
import y2k.joyreactor.common.ui
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.TagService

/**
 * Created by y2k on 3/8/16.
 */
class PostListViewModel(
    private val navigation: NavigationService,
    lifeCycleService: LifeCycleService,
    private val service: TagService,
    private val postService: PostService,
    private val group: Group) {

    val isBusy = property(false)
    val posts = property<ListWithDivider<PostItemViewModel>>()
    val hasNewPosts = property(false)
    val isError = property(false)

    init {
        service
            .queryPosts(group)
            .subscribe(lifeCycleService) {
                hasNewPosts += it.hasNew
                posts += it.posts
                    .map { PostItemViewModel(navigation, postService, it) }
                    .let { vms -> ListWithDivider(vms, it.divider) }
            }

        isBusy += true
        service
            .preloadNewPosts(group)
            .ui({ isBusy += false }, {
                isError += true
                isBusy += false
            })
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
}