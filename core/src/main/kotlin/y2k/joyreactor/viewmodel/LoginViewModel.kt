package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.openVM
import y2k.joyreactor.common.property
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 3/8/16.
 */
class LoginViewModel(
    private val navigation: NavigationService,
    private val service: ProfileService) {

    val username = property("")
    val password = property("")
    val isBusy = property(false)
    val isError = property(false)

    fun login() {
        async_ {
            isBusy += true
            isError += false
            try {
                await(service.login(username.value, password.value))
                navigation.openVM<ProfileViewModel>()
                navigation.close()
            } catch (e: Exception) {
                isError += true
            }
            isBusy += false
        }
    }

    fun register() = navigation.openBrowser("http://joyreactor.cc/register")
}