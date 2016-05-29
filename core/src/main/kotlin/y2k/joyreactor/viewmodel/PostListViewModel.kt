package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.await
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.property
import y2k.joyreactor.common.registerProperty
import y2k.joyreactor.model.Group
import y2k.joyreactor.model.Post
import y2k.joyreactor.services.*

/**
 * Created by y2k on 5/9/16.
 */
class PostListViewModel(
    private val navigationService: NavigationService,
    private val service: TagService,
    private val userService: UserService,
    private val lifeCycleService: LifeCycleService,
    private val postService: PostService) {

    val isBusy = property(false)
    val posts = property(emptyList<Post?>())
    val hasNewPosts = property(false)

    val quality = property(Group.Quality.Good)
    val group = property(Group.makeFeatured())

    private lateinit var state: StatelessPostListViewModel

    init {
        lifeCycleService.registerProperty(BroadcastService.TagSelected::class, group)
        group.subscribeLazy { changeCurrentGroup() }
        quality.subscribeLazy { changeCurrentGroup() }
        changeCurrentGroup(true)
    }

    fun changeCurrentGroup(isFirst: Boolean = false) {
        userService
            .makeGroup(group.value, quality.value)
            .await {
                if (!isFirst) {
                    state.isBusy.unsubscribe(isBusy)
                    state.posts.unsubscribe(posts)
                    state.hasNewPosts.unsubscribe(hasNewPosts)
                }

                state = StatelessPostListViewModel(navigationService, lifeCycleService, service, postService, it)

                state.isBusy.subscribe(isBusy)
                state.posts.subscribe(posts)
                state.hasNewPosts.subscribe(hasNewPosts)
            }
    }

    fun applyNew() = state.applyNew()
    fun loadMore() = state.loadMore()
    fun reloadFirstPage() = state.reloadFirstPage()

    fun postClicked(position: Int) = state.postClicked(position)
    fun playClicked(position: Int) = state.playClicked(position)
    fun changeLike(position: Int) = state.changeLike(position)
    fun toggleFavorite(position: Int) = state.toggleFavorite(position)
}