package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ListWithDivider
import y2k.joyreactor.common.WorkStatus
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.property
import y2k.joyreactor.common.registerProperty
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.ListState
import y2k.joyreactor.services.BroadcastService
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.Works
import kotlin.reflect.KClass

/**
 * Created by y2k on 5/9/16.
 */
class MainViewModel(
    val syncInBackground: (Works, Long) -> Unit,
    val watchForBackground: (Works, ((Long) -> WorkStatus) -> Unit) -> Unit,
    val queryPosts: (Long) -> CompletableFuture<ListState>,
    val navigateTo: (KClass<*>, Any?) -> Unit,
    scope: LifeCycleService) {

    val isBusy = property(false)
    val posts = property<ListWithDivider<PostItemViewModel>>()
    val hasNewPosts = property(false)
    val isError = property(false)

    val quality = property(Group.Quality.Good)
    private val group = property(Group.makeFeatured())

    init {
        scope.registerProperty(BroadcastService.TagSelected::class, group)
        quality.subscribe { syncInBackground(Works.syncPostsPreloadNewPosts, selected) }
        group.subscribe { syncInBackground(Works.syncPostsPreloadNewPosts, selected) }

        watchForBackground(Works.syncPosts) { getStatus ->
            async_ {
                isBusy += getStatus(selected).isInProgress
                isError += getStatus(selected).isFinishedWithError

                val data = await(queryPosts(selected))
                hasNewPosts += data.hasNew
                posts += data.posts
                    .map { PostItemViewModel(navigateTo, syncInBackground, it) }
                    .let { vms -> ListWithDivider(vms, data.divider) }
            }
        }
    }

    fun applyNew() = syncInBackground(Works.syncPostsApplyNew, selected)
    fun loadMore() = syncInBackground(Works.syncPostsLoadNextPage, selected)
    fun reloadFirstPage() = syncInBackground(Works.syncPostsReloadFirstPage, selected)

    fun openProfile() = navigateTo(ProfileViewModel::class, null)
    fun openMessages() = navigateTo(ThreadsViewModel::class, null)
    fun openAddTag() = navigateTo(AddTagViewModel::class, null)
    fun openFeedback() = navigateTo(CreateFeedback::class, null)

    private val selected: Long
        get() = Group(group.value, quality.value).id
}