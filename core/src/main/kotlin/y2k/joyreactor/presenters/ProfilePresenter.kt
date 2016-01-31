package y2k.joyreactor.presenters

import y2k.joyreactor.Profile
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
        service.get()
                .subscribe({ profile ->
                    view.setProfile(profile)
                    view.setBusy(false)
                }) { e ->
                    e.printStackTrace()
                    Navigation.instance.switchProfileToLogin()
                }
    }

    fun logout() {
        service.logout().subscribe({ Navigation.instance.switchProfileToLogin() })
    }

    interface View {

        fun setProfile(profile: Profile)

        fun setBusy(isBusy: Boolean)
    }
}