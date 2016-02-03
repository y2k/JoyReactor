package y2k.joyreactor.presenters

import y2k.joyreactor.Profile
import y2k.joyreactor.platform.Navigation
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
        profileService.getProfile().subscribe(
                { view.setUser(it) },
                { it.printStackTrace() })
    }

    fun create(commentText: String) {
        view.setIsBusy(true)
        service.createComment("2219757", commentText)
                .subscribe({
                    Navigation.instance.closeCreateComment()
                    view.setIsBusy(false)
                }, { it.printStackTrace() })
    }

    interface View {

        fun setIsBusy(isBusy: Boolean)

        fun setUser(profile: Profile)
    }
}