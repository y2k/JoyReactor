package y2k.joyreactor.presenters

import y2k.joyreactor.model.Profile
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.CommentService
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 10/4/15.
 */
class CreateCommentPresenter(
    private val view: CreateCommentPresenter.View,
    private val profileService: ProfileService,
    private val service: CommentService) {

    init {
        profileService
            .getProfile()
            .subscribeOnMain { view.setUser(it) }
    }

    fun create(commentText: String) {
        view.setIsBusy(true)
        service
            .createComment("2219757", commentText)
            .subscribeOnMain {
                NavigationService.instance.closeCreateComment()
                view.setIsBusy(false)
            }
    }

    interface View {

        fun setIsBusy(isBusy: Boolean)

        fun setUser(profile: Profile)
    }
}