package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.await
import y2k.joyreactor.common.property
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.platform.open
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 3/8/16.
 */
class LoginViewModel(
    private val navigationService: NavigationService,
    private val service: ProfileService) {

    val username = property("")
    val password = property("")
    val isBusy = property(false)
    val isError = property(false)

    fun login() {
        isBusy += true
        isError += false
        service
            .login(username.value, password.value)
            .await({
                isBusy += false
                navigationService.open<ProfileViewModel>()
                navigationService.close()
            }, {
                it.printStackTrace()
                isBusy += false
                isError += true
            })
    }

    fun register() {
        navigationService.openBrowser("http://joyreactor.cc/register")
    }
}