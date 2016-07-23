package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.ListWithDivider
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.openVM
import y2k.joyreactor.common.property
import y2k.joyreactor.common.registerProperty
import y2k.joyreactor.model.Group
import y2k.joyreactor.services.*

/**
 * Created by y2k on 5/9/16.
 */
class MainViewModel(
    private val navigation: NavigationService,
    private val service: TagService,
    private val userService: UserService,
    scope: LifeCycleService,
    private val postService: PostService,
    private val reportService: ReportService) {

    val isBusy = property(false)
    val posts = property<ListWithDivider<PostItemViewModel>>()
    val hasNewPosts = property(false)
    val isError = property(false)

    val quality = property(Group.Quality.Good)
    private val group = property(Group.makeFeatured())

//    private lateinit var state: PostListViewModel

    init {
        scope.registerProperty(BroadcastService.TagSelected::class, group)
//        group.subscribeLazy { changeCurrentGroup() }
//        quality.subscribeLazy { changeCurrentGroup() }
//        changeCurrentGroup(true)

//        scope(BroadcastService.TagSelected::class) {
//            group.value
//        }
        quality.subscribe { service.preloadNewPosts(groupWithQuality()) }
        group.subscribe { service.preloadNewPosts(groupWithQuality()) }
//        scope.register(BroadcastService.TagSelected::class) {
//            service.preloadNewPosts(myGroup())
//            group.value = it.group
//        }

        scope(service.getKeyFromWatchSync()) {
            async_ {
                service.getSyncStatus(groupWithQuality()).let {
                    isBusy += it.isInProgress
                    isError += it.isFinishedWithError
                }

                val status = await(service.queryPosts(groupWithQuality()))
                hasNewPosts += status.hasNew
                posts += status.posts
                    .map { PostItemViewModel(navigation, postService, it) }
                    .let { vms -> ListWithDivider(vms, status.divider) }
            }
        }
    }

//    init {
//        scope.registerProperty(BroadcastService.TagSelected::class, group)
////        group.subscribeLazy { changeCurrentGroup() }
////        quality.subscribeLazy { changeCurrentGroup() }
////        changeCurrentGroup(true)
//
////        scope(BroadcastService.TagSelected::class) {
////            group.value
////        }
//        quality.subscribe { service.preloadNewPosts(groupWithQuality()) }
//        group.subscribe { service.preloadNewPosts(groupWithQuality()) }
////        scope.register(BroadcastService.TagSelected::class) {
////            service.preloadNewPosts(myGroup())
////            group.value = it.group
////        }
//
//        scope(service.getKeyFromWatchSync()) {
//            async_ {
//                service.getSyncStatus(groupWithQuality()).let {
//                    isBusy += it.isInProgress
//                    isError += it.isFinishedWithError
//                }
//
//                val status = await(service.queryPosts(groupWithQuality()))
//                hasNewPosts += status.hasNew
//                posts += status.posts
//                    .map { PostItemViewModel(navigation, postService, it) }
//                    .let { vms -> ListWithDivider(vms, status.divider) }
//            }
//        }
//    }

//    fun changeCurrentGroup(isFirst: Boolean = false) {
//        async_ {
//            val it = await(userService.makeGroup(group.value, quality.value))
//
//            if (!isFirst) {
//                state.isBusy.unsubscribe(isBusy)
//                state.posts.unsubscribe(posts)
//                state.hasNewPosts.unsubscribe(hasNewPosts)
//                state.isError.unsubscribe(isError)
//            }
//
//            state = PostListViewModel(navigation, scope, service, postService, it)
//
//            state.isBusy.subscribe(isBusy)
//            state.posts.subscribe(posts)
//            state.hasNewPosts.subscribe(hasNewPosts)
//            state.isError.subscribe(isError)
//        }
//    }

//    fun applyNew() = state.applyNew()
//    fun loadMore() = state.loadMore()
//    fun reloadFirstPage() = state.reloadFirstPage()

    fun applyNew() = service.applyNew(groupWithQuality())
    fun loadMore() = service.loadNextPage(groupWithQuality())
    fun reloadFirstPage() = service.reloadFirstPage(groupWithQuality())

    fun openProfile() = navigation.openVM<ProfileViewModel>()
    fun openMessages() = navigation.openVM<ThreadsViewModel>()
    fun openAddTag() = navigation.openVM<AddTagViewModel>()
    fun openFeedback() = reportService.createFeedback()

    private fun groupWithQuality() = Group(group.value, quality.value)
}