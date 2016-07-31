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
    val executeBackgroundTask: (Works, Any) -> Unit,
    val waitBackgroundTask: (Works, () -> Unit) -> Unit,
    val statusBackgroundTask: (Works, Any) -> WorkStatus,
    val queryPosts: (String) -> CompletableFuture<ListState>,
    val navigateTo: (KClass<*>, Any?) -> Unit,
    scope: LifeCycleService) {

    val isBusy = property(false)
    val posts = property<ListWithDivider<PostItemViewModel>>()
    val hasNewPosts = property(false)
    val isError = property(false)

    val quality = property(Group.Quality.Good)
    val group = property(Group.makeFeatured().id)

    init {
        scope.registerProperty(BroadcastService.TagSelected::class, group)
        quality.subscribe { executeBackgroundTask(Works.syncPostsPreloadNewPosts, selected) }
        group.subscribe { executeBackgroundTask(Works.syncPostsPreloadNewPosts, selected) }

        waitBackgroundTask(Works.syncPosts) {
            async_ {
                val status = statusBackgroundTask(Works.syncPosts, selected)
                isBusy += status.isInProgress
                isError += status.isFinishedWithError

                val data = await(queryPosts(selected))
                hasNewPosts += data.hasNew
                posts += data.posts
                    .map { PostItemViewModel(navigateTo, executeBackgroundTask, it) }
                    .let { vms -> ListWithDivider(vms, data.divider) }
            }
        }
    }

    fun applyNew() = executeBackgroundTask(Works.syncPostsApplyNew, selected)
    fun loadMore() = executeBackgroundTask(Works.syncPostsLoadNextPage, selected)
    fun reloadFirstPage() = executeBackgroundTask(Works.syncPostsReloadFirstPage, selected)

    fun openProfile() = navigateTo(ProfileViewModel::class, null)
    fun openMessages() = navigateTo(ThreadsViewModel::class, null)
    fun openAddTag() = navigateTo(AddTagViewModel::class, null)
    fun openFeedback() = navigateTo(CreateFeedback::class, null)

    private val selected: String
        get() = Group(Group(id = group.value), quality.value).id
}