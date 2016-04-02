package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.binding
import y2k.joyreactor.common.await
import y2k.joyreactor.model.Image
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 3/8/16.
 */
class ProfileViewModel(
    private val navigationService: NavigationService,
    private val service: ProfileService) {

    val userImage = binding<Image>()
    val rating = binding(0f)
    val stars = binding(0f)
    val nextStarProgress = binding(0f)
    val userName = binding("")

    val isBusy = binding(false)

    init {
        isBusy += true
        service
            .getProfile()
            .await({
                userName += it.userName
                userImage += it.userImage
                rating += it.rating
                stars += it.stars.toFloat()
                nextStarProgress += it.progressToNewStar
                isBusy += false
            }, {
                it.printStackTrace()
                navigationService.switchProfileToLogin()
            })
    }

    fun logout() {
        service
            .logout()
            .await { navigationService.switchProfileToLogin() }
    }
}