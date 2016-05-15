package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.await
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Image
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.services.CommentService
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 3/8/16.
 */
class CreateCommentViewModel(
    private val profileService: ProfileService,
    private val service: CommentService,
    private val navigationService: NavigationService) {

    val isBusy = property(false)

    val username = property("")
    val avatar = property<Image>()

    val commentText = property("")

    init {
        profileService
            .getProfile()
            .await {
                username += it.userName
                avatar += it.userImage
            }
    }

    fun create() {
        isBusy += true
        service
            .createComment("2219757", commentText.value) // FIXME:
            .await {
                navigationService.close()
                isBusy += false
            }
    }
}