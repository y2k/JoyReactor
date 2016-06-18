package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.NotAuthorizedException
import y2k.joyreactor.common.ui
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.open
import y2k.joyreactor.common.property
import y2k.joyreactor.model.Image
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
    val isError = property(false)

    init {
        isBusy += true
        service
            .getProfile()
            .ui({
                userName += it.userName
                userImage += it.userImage
                rating += it.rating
                stars += it.stars.toFloat()
                nextStarProgress += it.progressToNewStar
                isBusy += false
            }, {
                it.printStackTrace()
                when (it) {
                    is NotAuthorizedException -> {
                        navigationService.open<LoginViewModel>()
                        navigationService.close()
                    }
                    else -> isError += true
                }
            })
    }

    fun logout() {
        service
            .logout()
            .ui {
                navigationService.open<LoginViewModel>()
                navigationService.close()
            }
    }
}