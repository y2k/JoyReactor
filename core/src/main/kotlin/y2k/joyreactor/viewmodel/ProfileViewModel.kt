package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.await
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Image
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.platform.open
import y2k.joyreactor.services.ProfileService

/**
 * Created by y2k on 3/8/16.
 */
class ProfileViewModel(
    private val navigationService: NavigationService,
    private val service: ProfileService) {

    val userImage = property<Image>()
    val rating = property(0f)
    val stars = property(0f)
    val nextStarProgress = property(0f)
    val userName = property("")

    val isBusy = property(false)

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
                navigationService.open<LoginViewModel>()
                navigationService.close()
            })
    }

    fun logout() {
        service
            .logout()
            .await {
                navigationService.open<LoginViewModel>()
                navigationService.close()
            }
    }
}