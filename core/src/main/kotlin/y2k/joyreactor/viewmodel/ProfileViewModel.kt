package y2k.joyreactor.viewmodel

import y2k.joyreactor.common.NotAuthorizedException
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.platform.NavigationService
import y2k.joyreactor.common.platform.openVM
import y2k.joyreactor.common.property
import y2k.joyreactor.common.ui
import y2k.joyreactor.model.Image
import y2k.joyreactor.model.Profile
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

    val awards = property(emptyList<Profile.Award>())

    init {
        async_ {
            isBusy += true
            try {
                val profile = await(service.getProfile())
                userName += profile.userName
                userImage += profile.userImage
                rating += profile.rating
                stars += profile.stars.toFloat()
                nextStarProgress += profile.progressToNewStar
                awards += profile.awards
                isBusy += false
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is NotAuthorizedException -> {
                        navigationService.openVM<LoginViewModel>()
                        navigationService.close()
                    }
                    else -> isError += true
                }
            }
            isBusy += false
        }
    }

    fun logout() {
        service.logout().ui {
            navigationService.openVM<LoginViewModel>()
            navigationService.close()
        }
    }
}