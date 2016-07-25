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
    val syncInBackground: (Works, Any) -> Unit,
    val watchForBackground: (Works, Any, (WorkStatus) -> Unit) -> Unit,
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
        quality.subscribe { syncInBackground(Works.syncPostsPreloadNewPosts, getGroup()) }
        group.subscribe { syncInBackground(Works.syncPostsPreloadNewPosts, getGroup()) }

        watchForBackground(Works.syncPosts, getGroupId()) { status ->
            async_ {
                isBusy += status.isInProgress
                isError += status.isFinishedWithError

                val data = await(queryPosts(getGroupId()))
                hasNewPosts += data.hasNew
                posts += data.posts
                    .map { PostItemViewModel(navigateTo, syncInBackground, it) }
                    .let { vms -> ListWithDivider(vms, data.divider) }
            }
        }
    }

    fun applyNew() = syncInBackground(Works.syncPostsApplyNew, getGroupId())
    fun loadMore() = syncInBackground(Works.syncPostsLoadNextPage, getGroupId())
    fun reloadFirstPage() = syncInBackground(Works.syncPostsReloadFirstPage, getGroupId())

    fun openProfile() = navigateTo(ProfileViewModel::class, null)
    fun openMessages() = navigateTo(ThreadsViewModel::class, null)
    fun openAddTag() = navigateTo(AddTagViewModel::class, null)
    fun openFeedback() = navigateTo(CreateFeedback::class, null)

    private fun getGroupId() = getGroup().id

    private fun getGroup() = Group(group.value, quality.value)
}