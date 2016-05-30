package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.Notifications
import y2k.joyreactor.common.await
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.common.property
import y2k.joyreactor.model.CommentGroup
import y2k.joyreactor.model.EmptyGroup
import y2k.joyreactor.services.LifeCycleService
import y2k.joyreactor.services.PostService
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 5/30/16.
 */
class CommentsViewModel(
    private val service: PostService,
    private val userService: ProfileService,
    private val navigation: NavigationService,
    private val lifeCycle: LifeCycleService) {

    val comments = property<CommentGroup>(EmptyGroup())
    val canCreateComments = property(false)

    init {
        val postId = navigation.argument.toLong()

        lifeCycle.register(Notifications.Post) {
            service.getComments(postId, 0).await { comments += it }
            userService.isAuthorized().await { canCreateComments += it }
        }
    }

    fun selectComment(position: Int) {
        val comment = comments.value[position]
        service.getCommentsAsync(comment.postId, comments.value.getNavigation(comment))
            .await { comments += it }
    }

    fun commentPost() {
        navigation.open<CreateCommentViewModel>(navigation.argument)
    }
}