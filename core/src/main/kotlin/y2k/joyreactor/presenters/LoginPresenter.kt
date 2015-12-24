package y2k.joyreactor.presenters

import y2k.joyreactor.platform.Navigation
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 9/29/15.
 */
class LoginPresenter(
        private val view: LoginPresenter.View,
        private val service: ProfileService) {

    fun login(username: String, password: String) {
        view.setBusy(true)
        service.login(username, password)
                .subscribe({
                    view.setBusy(false)
                    Navigation.getInstance().switchLoginToProfile()
                }, { error ->
                    error.printStackTrace()
                    view.setBusy(false)
                    view.showError()
                })
    }

    fun register() {
        view.openUrl("http://joyreactor.cc/register")
    }

    interface View {

        fun setBusy(isBusy: Boolean)

        fun showError()

        fun openUrl(url: String)
    }
}