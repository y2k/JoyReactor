package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ListWithDivider
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.TagService

/**
 * Created by y2k on 3/8/16.
 */
class PostListViewModel(
    private val navigation: NavigationService,
    scope: LifeCycleService,
    private val service: TagService,
    private val postService: PostService,
    private val group: Group) {

    val isBusy = property(false)
    val posts = property<ListWithDivider<PostItemViewModel>>()
    val hasNewPosts = property(false)
    val isError = property(false)

    init {
        scope(service.preloadNewPosts(group)) {
            async_ {
                service.getSyncStatus(group).let {
                    isBusy += it.isInProgress
                    isError += it.isFinishedWithError
                }

                val status = await(service.queryPosts(group))
                hasNewPosts += status.hasNew
                posts += status.posts
                    .map { PostItemViewModel(navigation, postService, it) }
                    .let { vms -> ListWithDivider(vms, status.divider) }
            }
        }
    }

    // ==============================================================
    // Commands
    // ==============================================================

    fun applyNew() = service.applyNew(group)
    fun loadMore() = service.loadNextPage(group)
    fun reloadFirstPage() = service.reloadFirstPage(group)
}