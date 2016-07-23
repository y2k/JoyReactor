package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.openVM
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Comment
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
    scope: LifeCycleService) {

    val comments = property<CommentGroup>(EmptyGroup())
    val canCreateComments = property(false)

    init {
        val commentId = navigation.argument.toLong()
        async_ {
            comments += service.getCommentsForId(commentId)
            canCreateComments += await(userService.isAuthorized())
        }
    }

    fun selectComment(comment: Comment) {
        async_ {
            val navId = comments.value.getNavigation(comment)
            comments += await(service.getCommentsAsync(comment.postId, navId))
        }
    }

    fun commentPost() = navigation.openVM<CreateCommentViewModel>(navigation.argument)
}