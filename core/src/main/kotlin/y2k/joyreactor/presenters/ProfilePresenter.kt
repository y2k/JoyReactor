package y2k.joyreactor.presenters

import y2k.joyreactor.model.Profile
import y2k.joyreactor.common.subscribeOnMain
import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 9/30/15.
 */
class ProfilePresenter(
    private val view: ProfilePresenter.View,
    private val service: ProfileService) {

    init {
        view.setBusy(true)
        service
            .getProfile()
            .subscribeOnMain({
                view.setProfile(it)
                view.setBusy(false)
            }, {
                it.printStackTrace()
                Navigation.instance.switchProfileToLogin()
            })
    }

    fun logout() {
        service.logout().subscribeOnMain { Navigation.instance.switchProfileToLogin() }
    }

    interface View {

        fun setProfile(profile: Profile)

        fun setBusy(isBusy: Boolean)
    }
}