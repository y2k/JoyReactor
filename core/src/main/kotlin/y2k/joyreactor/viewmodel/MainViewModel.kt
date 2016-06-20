package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.common.property
import y2k.joyreactor.common.registerProperty
import y2k.joyreactor.common.ui
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.*

/**
 * Created by y2k on 5/9/16.
 */
class MainViewModel(
    private val navigation: NavigationService,
    private val service: TagService,
    private val userService: UserService,
    private val lifeCycleService: LifeCycleService,
    private val postService: PostService,
    private val reportService: ReportService) {

    val isBusy = property(false)
    val posts = property(emptyList<Post?>())
    val hasNewPosts = property(false)
    val isError = property(false)

    val quality = property(Group.Quality.Good)
    val group = property(Group.makeFeatured())

    private lateinit var state: PostListViewModel

    init {
        lifeCycleService.registerProperty(BroadcastService.TagSelected::class, group)
        group.subscribeLazy { changeCurrentGroup() }
        quality.subscribeLazy { changeCurrentGroup() }
        changeCurrentGroup(true)
    }

    fun changeCurrentGroup(isFirst: Boolean = false) {
        userService
            .makeGroup(group.value, quality.value)
            .ui {
                if (!isFirst) {
                    state.isBusy.unsubscribe(isBusy)
                    state.posts.unsubscribe(posts)
                    state.hasNewPosts.unsubscribe(hasNewPosts)
                    state.isError.unsubscribe(isError)
                }

                state = PostListViewModel(navigation, lifeCycleService, service, postService, it)

                state.isBusy.subscribe(isBusy)
                state.posts.subscribe(posts)
                state.hasNewPosts.subscribe(hasNewPosts)
                state.isError.subscribe(isError)
            }
    }

    fun applyNew() = state.applyNew()
    fun loadMore() = state.loadMore()
    fun reloadFirstPage() = state.reloadFirstPage()

    fun postClicked(position: Int) = state.postClicked(position)
    fun playClicked(position: Int) = state.playClicked(position)
    fun changeLike(position: Int) = state.changeLike(position)
    fun toggleFavorite(position: Int) = state.toggleFavorite(position)

    fun openProfile() = navigation.open<ProfileViewModel>()
    fun openMessages() = navigation.open<ThreadsViewModel>()
    fun openAddTag() = navigation.open<AddTagViewModel>()
    fun openFeedback() = reportService.createFeedback()
}