package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ListWithDivider
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.property
import y2k.joyreactor.common.registerProperty
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 5/9/16.
 */
class MainViewModel(
    private val navigateTo: (KClass<*>, Any?) -> Unit,
    private val service: TagService,
    scope: LifeCycleService,
    private val postService: PostService,
    private val reportService: ReportService) {

    val isBusy = property(false)
    val posts = property<ListWithDivider<PostItemViewModel>>()
    val hasNewPosts = property(false)
    val isError = property(false)

    val quality = property(Group.Quality.Good)
    private val group = property(Group.makeFeatured())

    init {
        scope.registerProperty(BroadcastService.TagSelected::class, group)
        quality.subscribe { service.preloadNewPosts(groupWithQuality()) }
        group.subscribe { service.preloadNewPosts(groupWithQuality()) }

        scope(service.getKeyFromWatchSync()) {
            async_ {
                service.getSyncStatus(groupWithQuality()).let {
                    isBusy += it.isInProgress
                    isError += it.isFinishedWithError
                }

                val status = await(service.queryPosts(groupWithQuality()))
                hasNewPosts += status.hasNew
                posts += status.posts
                    .map { PostItemViewModel(navigateTo, postService, it) }
                    .let { vms -> ListWithDivider(vms, status.divider) }
            }
        }
    }

    private fun groupWithQuality() = Group(group.value, quality.value)

    fun applyNew() = service.applyNew(groupWithQuality())
    fun loadMore() = service.loadNextPage(groupWithQuality())

    fun reloadFirstPage() = service.reloadFirstPage(groupWithQuality())
    fun openProfile() = navigateTo(ProfileViewModel::class, null)
    fun openMessages() = navigateTo(ThreadsViewModel::class, null)
    fun openAddTag() = navigateTo(AddTagViewModel::class, null)

    fun openFeedback() = reportService.createFeedback()
}