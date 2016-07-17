package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.getArgument
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Image
import y2k.joyreactor.services.CommentService
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 3/8/16.
 */
class CreateCommentViewModel(
    private val profileService: ProfileService,
    private val service: CommentService,
    private val navigation: NavigationService) {

    val isBusy = property(false)

    val username = property("")
    val avatar = property<Image>()

    val commentText = property("")

    val postId = navigation.getArgument<Long>()

    init {
        async_ {
            val profile = await(profileService.getProfile())
            username += profile.userName
            avatar += profile.userImage
        }
    }

    fun create() {
        async_ {
            isBusy += true
            await(service.createComment(postId, commentText.value))
            navigation.close()
        }
    }
}