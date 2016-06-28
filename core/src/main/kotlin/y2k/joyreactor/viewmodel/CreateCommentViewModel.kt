package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.property
import y2k.joyreactor.common.ui
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

    init {
        profileService.getProfile().ui {
            username += it.userName
            avatar += it.userImage
        }
    }

    fun create() {
        isBusy += true
        service.createComment(navigation.argument.toLong(), commentText.value).ui {
            navigation.close()
            isBusy += false
        }
    }
}